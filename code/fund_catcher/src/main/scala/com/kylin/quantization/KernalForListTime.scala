package com.kylin.quantization

import java.io.IOException

import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.util.JedisUtil.JedisRunner
import com.kylin.quantization.util.{JedisUtil, RowKeyUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.{CompareFilter, FilterList, QualifierFilter, RegexStringComparator}
import org.apache.spark.{AccumulatorParam, SparkConf, SparkContext}

import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkContext
import org.apache.spark.mllib.stat.KernelDensity
import org.apache.spark.sql.SQLContext
import redis.clients.jedis.Jedis
import org.apache.spark._

import scala.util.control.Breaks

/**
  * ClassName: KernalForFxrq
  * Description:
  * Author: aierxuan
  * Date: 2019-02-04 11:06
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
object KernalForListTime extends ScalaBaseSparkMain{
  val SQL_TAB="kernal_list"//sql标签
  val BAND_WIDTH=0.2//核密度带宽
  val RECTANGLE_COUNT=20//直方图分组的个数
  val IS_TEST=false

  def main(args: Array[String]): Unit = {
    var sparkContext =new SparkContext(sparkConf())
    var sqlContext=new SQLContext(sparkContext)
    var df=sql(SQL_TAB,sparkContext,sqlContext)

    var decimalRdd=df.rdd.map(r=>{
      r.getDecimal(0)
    })
    var max=decimalRdd.reduce((a,b)=>if(a.compareTo(b)>0) a else b)
    var min=decimalRdd.reduce((a,b)=>if(a.compareTo(b)<0) a else b)
    val kd=new KernelDensity().setSample(decimalRdd.map[Double](r=>r.doubleValue())).setBandwidth(BAND_WIDTH)
    if(IS_TEST){
      var collect=df.rdd.collect()
      var i=0
      collect.foreach(c=>{
        if(i%20==0){
          println("")
        }
        print(c+" , ")
        i+=1
      })
      println("min"+min)
      println("max"+max)
      sparkContext.stop()
      return
    }

    val splitList=splitByMinMax(min,max)
    //    var list: List[Double] = List()
    var list=splitList.flatMap(m=>{
      var cha=m.get("big").get.-(m.get("small").get)
      var step=cha./(5.0000)
      var result=List[Double]()
      for(i<-Range(0,5)){
        result=result.+:(m.get("small").get.+(step*i).toDouble)
      }
      result.reverse
    }).toList

    /*for(i<-Range(Math.floor(min.doubleValue()).toInt*100,Math.floor(max.doubleValue()).toInt*100,KERNAL_STEP)){
      list=list.+:(i.toDouble/100.00)
    }*/


    println(list)

    /*var kernalLebelStr=list.map(a=>a.toString).reduce((a1,a2)=>a1+","+a2)
    kernalLebelStr="["+kernalLebelStr+"]";
    val densities = kd.estimate(list.toArray)
    var densitiesStr=densities.map(d=>d.toString).reduce((a1,a2)=>a1+","+a2)
    densitiesStr="["+densitiesStr+"]";*/
    val densities = kd.estimate(list.toArray)
    var densitiesStr="["
    for(i <- Range(0,list.size)){
      densitiesStr=densitiesStr+"["+list.apply(i)+","+densities.apply(i)+"],"
    }
    densitiesStr=densitiesStr.substring(0,densitiesStr.size-1)
    densitiesStr=densitiesStr+"]"



    var rectangleMap=Map[String,Int]()
    decimalRdd.collect().foreach(d=>{

      var loop2=new Breaks
      loop2.breakable{
        for (s<-splitList){
          val smin=s.get("small").get
          val smax=s.get("big").get
          if( (d.doubleValue()>=smin.doubleValue() && d.doubleValue()<smax.doubleValue())   ){
            var v=rectangleMap.get(smin.toString()+"-"+smax.toString())
            if(v.isEmpty){
              rectangleMap+=(smin.toString()+"-"+smax.toString()->1)
            }else{
              rectangleMap+=(smin.toString()+"-"+smax.toString()->(v.get+1))
            }

            loop2.break()
          }
        }
      }
    })



    /*var rectangleLabelStr=splitList.map(m=>"'"+m.get("small").get+"-"+m.get("big").get+"'").reduce((a1,a2)=>a1+","+a2)
    rectangleLabelStr="["+rectangleLabelStr+"]"

    var rectangleDataStr=splitList.map(m=>{
      var value=rectangleMap.get(m.get("small").get+"-"+m.get("big").get)
      if(value.isEmpty) "0" else value.get.toString
    } ).reduce((a,b)=>a + "," + b)
    rectangleDataStr="["+rectangleDataStr+"]"*/
    var rectangleLabelStr=splitList.map(m=>"'"+m.get("small").get+"-"+m.get("big").get+"'").reduce((a1,a2)=>a1+","+a2)
    rectangleLabelStr="["+rectangleLabelStr+"]"
    var rectangleDataStr=splitList.map(m=>{
      var value=rectangleMap.get(m.get("small").get+"-"+m.get("big").get)
      var c=if(value.isEmpty) "0" else value.get.toString
      "["+m.get("small").get.+(m.get("big").get)./(BigDecimal(2.0000))+","+c+"]"
    } ).reduce((a,b)=>a + "," + b)
    rectangleDataStr="["+rectangleDataStr+"]"



    /*var count=decimalRdd.count();
    var cdf=list.map(v=>BigDecimal(decimalRdd.filter(d=>d.doubleValue()<=v).count())./(BigDecimal(count)).toString()).reduce((a1,a2)=>a1+","+a2)
    var cdfStr="["+cdf+"]"*/
    var count=decimalRdd.count();
    var broadcast=sparkContext.broadcast(BigDecimal(count))
    JedisUtil.set("accumulator","1")
    var cdfArr=decimalRdd.sortBy(b=>b).map(b=>{
      var bcount=broadcast.value
      var percent=BigDecimal(JedisUtil.get("accumulator"))./(bcount)
      JedisUtil.incr("accumulator")
      Tuple2(b,percent)
    }).collect()
    var cdfDataStr="["+cdfArr.map(c=>"["+c._1.toString+","+c._2.toString+"]").reduce((c1,c2)=>c1+","+c2)+"]"



    /**开始计算箱型图**/
    var boxData=decimalRdd.map(d=>d.toString).reduce((d1,d2)=>d1+","+d2)
    var boxDataStr="[["+boxData+"]]"
    /**结束计算箱型图**/






    println("============== kernal data:")
//    println("kernalLebelStr  ===>"+kernalLebelStr)
    println("densitiesStr  ===>"+densitiesStr)
    println("============== rectangle data:")
    println("rectangleLabelStr  ===>"+rectangleLabelStr)
    println("rectangleDataStr  ===>"+rectangleDataStr)
//    println("cdfStr  ===>"+cdfStr)
    JedisUtil.jedis[Object](new JedisRunner[Object] {
      override def run(jedis: Jedis): Object = {
//        jedis.set("kernalLebelStr",kernalLebelStr)
        jedis.set("densitiesStr",densitiesStr)
        jedis.set("rectangleLabelStr",rectangleLabelStr)
        jedis.set("rectangleDataStr",rectangleDataStr)
//        jedis.set("cdfStr",cdfStr)
        jedis.set("cdfDataStr",cdfDataStr)
        jedis.set("boxData",boxDataStr)
      }
    })
    JedisUtil.destroy()


  }




  def splitByMinMax(min: BigDecimal,max: BigDecimal): List[Map[String,BigDecimal]] = {
    var max1=max.+(BigDecimal("0.0001"))
    var min1=min.-(BigDecimal("0.0001"))
    var  rectangleList: List[Map[String,BigDecimal]] = List()
    val stepLen=(max1.-(min1))./(RECTANGLE_COUNT)
    var small=min1
    var big=BigDecimal(0.0)
    // 创建 Breaks 对象
    val loop = new Breaks;
    loop.breakable{
      while (true){
        big=small.+(stepLen)
        if(big>max1){
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
    scan.setStartRow(Bytes.toBytes(RowKeyUtil.getKlineRowkey(".INX","19490101")))
    scan.setStopRow(Bytes.toBytes(RowKeyUtil.getKlineRowkey(".INX","20190216")))
    try
      hconf.set(TableInputFormat.SCAN, BaseSparkMain.convertScanToString(scan))
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
    Map[String,Configuration]("index_inx" -> hconf)

  }
}
