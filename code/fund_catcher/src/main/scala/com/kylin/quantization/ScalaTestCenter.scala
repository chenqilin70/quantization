package com.kylin.quantization

import java.io.IOException

import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.util.ResultUtil
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkContext
import org.apache.spark.mllib.stat.KernelDensity


/**
  * ClassName: ScalaTestCenter
  * Description:
  * Author: aierxuan
  * Date: 2019-01-31 11:41
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
object ScalaTestCenter extends ScalaBaseSparkMain{

  def main(args: Array[String]): Unit = {
    /*var list: List[Double] = List()
    list=list.+:(12.2)
    list=list.+:(12.3)
    println(list)*/

    val sparkContext=new SparkContext(sparkConf());
    var tableRdd=sparkContext newAPIHadoopRDD(getFundListConf(), classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
    var rdd=tableRdd.map((t)=>{
      var result=t._2;
      var zcgm=ResultUtil.strVal(result,"baseinfo","zcgm")
      var jjdm=ResultUtil.strVal(result,"baseinfo","jjdm")
      var zcgmDecimal= BigDecimal("3.40亿元（截止至：2018年12月31日）".replaceAll("（.+）", "").replaceAll("亿元", "")).*(BigDecimal("100000000"))
      Tuple2[String,BigDecimal](jjdm,zcgmDecimal);
    }).reduceByKey((g1,g2)=>g1).map(t=>t._2.toDouble)

    var sum=rdd.reduce((d1,d2)=>d1+d2)
    var count=rdd.map(d=>1).reduce((c1,c2)=>c1+c2);
    var max=rdd.max();
    var min=rdd.min();
    var bandWidth=sum/count
    println("bandWidth is "+bandWidth)
    println("min is "+min)
    println("max is "+max)
    println("count is "+sum)

    val kd=new KernelDensity()
      .setSample(rdd)
      .setBandwidth(bandWidth)

    var list: List[Double] = List()
    for(i<-Range(Math.floor(min).toInt,Math.floor(max).toInt,100000000)){
      list=list.+:(i.toDouble)
    }
    list=list.reverse

    val densities = kd.estimate(list.toArray)
    println(list)
    println("====================================================")
    println(densities.toList)






    /*val sparkContext=new SparkContext(sparkConf());
    var tableRdd=sparkContext.parallelize(Seq(10.0,30.0,50.0,50.0))

    val kd=new KernelDensity()
      .setSample(tableRdd)
      .setBandwidth(7)
    var list: List[Double] = List()
    for(i<-Range(0,700,5)){
      val i1=i.toDouble/10
      list=list.+:(i1)
    }
    list=list.reverse

    val densities = kd.estimate(list.toArray)
    println(list)
    println(densities.toList)*/

  }

  private def getFundListConf(): Configuration= {
    val hconf = HBaseConfiguration.create
    //需要读取的hbase表名
    val tableName = "fund"
    hconf.set(TableInputFormat.INPUT_TABLE, tableName)
    val filter1 = new SingleColumnValueFilter(Bytes.toBytes("baseinfo"), Bytes.toBytes("jjlx"), CompareFilter.CompareOp.EQUAL, new RegexStringComparator("股票型"))
    val filter4 = new SingleColumnValueFilter(Bytes.toBytes("baseinfo"), Bytes.toBytes("zcgm"), CompareFilter.CompareOp.NOT_EQUAL, new RegexStringComparator("---"))
    val filter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("zcgm"))
    val filter3 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("jjdm"))
    val qulifierFilterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, filter2, filter3)
    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, filter1,filter4, qulifierFilterList)
    val scan = new Scan().setFilter(filterList)
    try
      hconf.set(TableInputFormat.SCAN, BaseSparkMain.convertScanToString(scan))
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
    hconf
  }

}






