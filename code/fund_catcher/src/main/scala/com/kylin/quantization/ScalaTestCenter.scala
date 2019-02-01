package com.kylin.quantization

import java.io.IOException
import java.text.{FieldPosition, NumberFormat, ParsePosition}

import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.util.{ExceptionTool, ResultUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkContext
import org.apache.spark.mllib.stat.KernelDensity
import scala.util.control._
import scala.math


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
  def stream(i: Long = 1): Stream[Long] = i #:: stream(i + 1)
  def splitByMinMax(min: BigDecimal,max: BigDecimal): List[Map[String,BigDecimal]] = {
    var  rectangleList: List[Map[String,BigDecimal]] = List()
    val stepLen=(max.-(min))./(80)
    var small=min
    var big=BigDecimal(0.0)
    // 创建 Breaks 对象
    val loop = new Breaks;
    loop.breakable{
      while (true){
        big=small.+(stepLen)
        if(big>max){
          loop.break()
        }
        rectangleList=rectangleList.+:(Map("small"->small,"big"->big))
        small=big

      }
    }
    rectangleList.reverse
  }

  def main1(args: Array[String]): Unit = {
    print(splitByMinMax(-0.1,20.1))
  }
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
      var zcgmDecimal=BigDecimal("0")
      try{
        zcgmDecimal= BigDecimal(zcgm.replaceAll("（.+）", "").replaceAll("亿元", "").trim())
      }catch {
        case ex:Exception =>{
          println("zcgm :"+zcgm+","+ExceptionTool.toString(ex))
          throw new RuntimeException("zcgm :"+zcgm)
        }
      }

      Tuple2[String,BigDecimal](jjdm,zcgmDecimal);
    }).reduceByKey((g1,g2)=>g1).map(t=>t._2.toDouble)
    var sum=rdd.reduce((d1,d2)=>d1+d2)
    var count=rdd.map(d=>1).reduce((c1,c2)=>c1+c2);
    var max=rdd.max();
    var min=rdd.min();
//    var bandWidth=BigDecimal(sum.toDouble)./(BigDecimal(count.toDouble)).toDouble
    var bandWidth=2.5
    println("bandWidth is "+bandWidth)
    println("min is "+min)
    println("max is "+max)
    println("count is "+sum)

    val kd=new KernelDensity()
      .setSample(rdd)
      .setBandwidth(bandWidth)

    var list: List[Double] = List()
    for(i<-Range(Math.floor(min).toInt,Math.floor(max).toInt,1)){
      list=list.+:(i.toDouble)
    }
    list=list.reverse

    val densities = kd.estimate(list.toArray)
    println("=====================核密度估计：bandWidth："+bandWidth)
    println(list)

    println(densities.toList)



    val splitList=splitByMinMax(BigDecimal(min),BigDecimal(max))
    print(splitList)
    var rectangleTs= rdd.map(d=>{
      var tuple:Tuple2[String,Int]=null
      var loop2=new Breaks
      loop2.breakable{
        for (s<-splitList){
          val smin=s.get("small").get.setScale(4,BigDecimal.RoundingMode.HALF_UP)
          val smax=s.get("big").get.setScale(4,BigDecimal.RoundingMode.HALF_UP)
          if( (d>=smin && d<smax)  || (d.equals(max) && d.equals(smax.toDouble)) ){
            tuple=new Tuple2[String,Int](smin.toString()+"-"+smax.toString(),1)
            loop2.break()
          }else{
//            println("tuple is null d:"+d+",smax:"+smax.toString()+",max:"+max)
          }
        }
      }
      tuple
    }).reduceByKey((d1,d2)=>d1+d2)


    println("============== rectangle data:")
    var labelStr=splitList.map(m=>m.get("small").get.setScale(4,BigDecimal.RoundingMode.HALF_UP)+"-"+m.get("big").get.setScale(4,BigDecimal.RoundingMode.HALF_UP))

    println(labelStr)
    var rectangleMap=rectangleTs.collectAsMap()

    var dataStr=splitList.map(m=>{
      var value=rectangleMap.get(m.get("small").get.setScale(4,BigDecimal.RoundingMode.HALF_UP)+"-"+m.get("big").get.setScale(4,BigDecimal.RoundingMode.HALF_UP))
      if(value.isEmpty) "0" else value.get.toString
    } ).reduce((a,b)=>a + "," + b)
    println(dataStr)











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
//    val filter1 = new SingleColumnValueFilter(Bytes.toBytes("baseinfo"), Bytes.toBytes("jjlx"), CompareFilter.CompareOp.EQUAL, new RegexStringComparator("股票型"))
    val filter4 = new SingleColumnValueFilter(Bytes.toBytes("baseinfo"), Bytes.toBytes("zcgm"), CompareFilter.CompareOp.NOT_EQUAL, new RegexStringComparator("---"))
    val filter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("zcgm"))
    val filter3 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("jjdm"))
    val qulifierFilterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, filter2, filter3)
    val filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL/*, filter1*/,filter4, qulifierFilterList)
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






