package com.kylin.quantization

import java.io.IOException

import com.kylin.quantization.KernelForZcgm.splitByMinMax
import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.util.JedisUtil.JedisRunner
import com.kylin.quantization.util.{JedisUtil, RowKeyUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkContext
import org.apache.spark.mllib.stat.KernelDensity
import org.apache.spark.sql.SQLContext
import redis.clients.jedis.Jedis

import scala.util.control.Breaks

/**
  * 标普500的percent核密度估计
  * ClassName: KernalForFxrq
  * Description:
  * Author: aierxuan
  * Date: 2019-02-04 11:06
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
object KernelForPercent extends ScalaBaseSparkMain{
  val SQL_TAB="kernal_percent"//sql标签
  val BAND_WIDTH=0.2//核密度带宽
  val RECTANGLE_COUNT=200//直方图分组的个数
  val IS_TEST=false

  def main(args: Array[String]): Unit = {
    var sparkContext =new SparkContext(sparkConf())
    var sqlContext=new SQLContext(sparkContext)
    var df=sql(SQL_TAB,sparkContext,sqlContext)

    /**开始准备公用数据项**/
    var decimalRdd=df.rdd.map(r=>r.getDecimal(0))
    var max=decimalRdd.reduce((a,b)=>if(a.compareTo(b)>0) a else b)
    var min=decimalRdd.reduce((a,b)=>if(a.compareTo(b)<0) a else b)
    val kd=new KernelDensity().setSample(decimalRdd.map[Double](r=>r.doubleValue())).setBandwidth(BAND_WIDTH)
    if(IS_TEST){}
    val splitList=splitByMinMax(min,max)
    //    var list: List[Double] = List()
    var list=splitList.flatMap(m=>{
      var cha=m.get("big").get.-(m.get("small").get)
      var step=cha./(5.0000)
      var result=List[Double]()
      for(i<-Range(1,6)){
        result=result.+:(m.get("small").get.+(step*i).toDouble)
      }
      result.reverse
    }).toList

    /*for(i<-Range(Math.floor(min.doubleValue()).toInt*100,Math.floor(max.doubleValue()).toInt*100,KERNAL_STEP)){
      list=list.+:(i.toDouble/100.00)
    }*/

    /**结束准备公用数据项**/



    /**开始计算和密度估计**/
    var kernalLebelStr=list.map(a=>a.toString).reduce((a1,a2)=>a1+","+a2)
    kernalLebelStr="["+kernalLebelStr+"]";
    val densities = kd.estimate(list.toArray)
    var densitiesStr=densities.map(d=>d.toString).reduce((a1,a2)=>a1+","+a2)
    densitiesStr="["+densitiesStr+"]";
    /**结束计算和密度估计**/


    /**开始计算累计分布函数**/
    var count=decimalRdd.count();
//    var cdf=list.map(v=>BigDecimal(decimalRdd.filter(d=>d.doubleValue()<=v).count())./(BigDecimal(count)).toString()).reduce((a1,a2)=>a1+","+a2)
//    decimalRdd.sortBy(d=>d.doubleValue())


    var broadcast=sparkContext.broadcast(BigDecimal(count))
    JedisUtil.set("accumulator","1")
    var cdfArr=decimalRdd.sortBy(b=>b).map(b=>{
      var bcount=broadcast.value
      var percent=BigDecimal(JedisUtil.get("accumulator"))./(bcount)

      JedisUtil.incr("accumulator")
      Tuple2(b,percent)
    }).collect()
    var cdfLabelStr="["+cdfArr.map(c=>c._1.toString).reduce((c1,c2)=>c1+","+c2)+"]"
    var cdfDataStr="["+cdfArr.map(c=>c._2.toString).reduce((c1,c2)=>c1+","+c2)+"]"
    /**结束计算累计分布函数**/


    /**开始计算箱型图**/

    /**结束计算箱型图**/




    /**开始计算条形图数据**/
    var rectangleTs= decimalRdd.map(d=>{
      var tuple:Tuple2[String,Int]=null
      var loop2=new Breaks
      loop2.breakable{
        for (s<-splitList){
          val smin=s.get("small").get
          val smax=s.get("big").get
          if( (d.doubleValue()>=smin.doubleValue() && d.doubleValue()<smax.doubleValue())  ||
            (d.doubleValue().equals(max.doubleValue()) && d.doubleValue().equals(smax.doubleValue())) ){
            tuple=new Tuple2[String,Int](smin.toString()+"-"+smax.toString(),1)
            loop2.break()
          }
        }
      }
      tuple
    }).reduceByKey((d1,d2)=>d1+d2)


    var rectangleLabelStr=splitList.map(m=>"'"+m.get("small").get+"-"+m.get("big").get+"'").reduce((a1,a2)=>a1+","+a2)
    rectangleLabelStr="["+rectangleLabelStr+"]"
    var rectangleMap=rectangleTs.collectAsMap()
    var rectangleDataStr=splitList.map(m=>{
      var value=rectangleMap.get(m.get("small").get+"-"+m.get("big").get)
      if(value.isEmpty) "0" else value.get.toString
    } ).reduce((a,b)=>a + "," + b)
    rectangleDataStr="["+rectangleDataStr+"]"
    /**结束计算条形图数据**/







    println("============== kernal data:")
    println("kernalLebelStr  ===>"+kernalLebelStr)
    println("densitiesStr  ===>"+densitiesStr)
    println("============== rectangle data:")
    println("rectangleLabelStr  ===>"+rectangleLabelStr)
    println("rectangleDataStr  ===>"+rectangleDataStr)
    println("cdfLabelStr  ===>"+cdfLabelStr)
    JedisUtil.jedis[Object](new JedisRunner[Object] {
      override def run(jedis: Jedis): Object = {
        jedis.set("kernalLebelStr",kernalLebelStr)
        jedis.set("densitiesStr",densitiesStr)
        jedis.set("rectangleLabelStr",rectangleLabelStr)
        jedis.set("rectangleDataStr",rectangleDataStr)
        jedis.set("cdfDataStr",cdfDataStr)
      }
    })
    JedisUtil.destroy()


  }




  def splitByMinMax(min: BigDecimal,max: BigDecimal): List[Map[String,BigDecimal]] = {
    var  rectangleList: List[Map[String,BigDecimal]] = List()
    val stepLen=(max.-(min))./(RECTANGLE_COUNT)
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
        rectangleList=rectangleList.+:(Map("small"->small.setScale(4,BigDecimal.RoundingMode.HALF_UP),"big"->big.setScale(4,BigDecimal.RoundingMode.HALF_UP)))
        small=big

      }
    }
    rectangleList.reverse
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    val hconf = HBaseConfiguration.create
    //需要读取的hbase表名
    val tableName = "index"
    hconf.set(TableInputFormat.INPUT_TABLE, tableName)
    val filter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("percent"))
    val filter3 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("key"))
    val qulifierFilterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, filter2, filter3)
    val scan = new Scan().setFilter(qulifierFilterList)
    scan.setStartRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey(".INX","19490101")))
    scan.setStopRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey(".INX","20190216")))
    try
      hconf.set(TableInputFormat.SCAN, BaseSparkMain.convertScanToString(scan))
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
    Map[String,Configuration]("index_inx" -> hconf)

  }
}
