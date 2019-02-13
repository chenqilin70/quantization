package com.kylin.quantization

import com.kylin.quantization.KernelForZcgm.splitByMinMax
import org.apache.spark.SparkContext
import org.apache.spark.mllib.stat.KernelDensity
import org.apache.spark.sql.SQLContext

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
object KernalMain extends ScalaBaseSparkMain{
  val SQL_TAB="kernal"//sql标签
  val BAND_WIDTH=2//核密度贷款
  val KERNAL_STEP=1//核密度曲线的步长
  val RECTANGLE_COUNT=500//直方图分组的个数
  val IS_TEST=false

  def main(args: Array[String]): Unit = {
    var sparkContext =new SparkContext(sparkConf())
    var sqlContext=new SQLContext(sparkContext)
    var df=sql(SQL_TAB,sparkContext,sqlContext)
    if(IS_TEST){
      df.show()
      return
    }
    var doubleRdd=df.rdd.map(r=>{
      println("r "+r)
      println("size "+r.size)
      println("getDouble "+r.getDouble(0))
      r.getDouble(0)
    })
    var max=doubleRdd.max();
    var min=doubleRdd.min();
    val kd=new KernelDensity().setSample(doubleRdd.map(i=>i.toDouble)).setBandwidth(BAND_WIDTH)



    var list: List[Double] = List()
    for(i<-Range(Math.floor(min).toInt,Math.floor(max).toInt,KERNAL_STEP)){
      list=list.+:(i.toDouble)
    }
    list=list.reverse
    var kernalLebelStr=list.map(a=>a.toString).reduce((a1,a2)=>a1+","+a2)
    val densities = kd.estimate(list.toArray)
    var densitiesStr=densities.map(d=>d.toString).reduce((a1,a2)=>a1+","+a2)


    val splitList=splitByMinMax(BigDecimal(min),BigDecimal(max))
    var rectangleTs= doubleRdd.map(d=>{
      var tuple:Tuple2[String,Int]=null
      var loop2=new Breaks
      loop2.breakable{
        for (s<-splitList){
          val smin=s.get("small").get
          val smax=s.get("big").get
          if( (d>=smin && d<smax)  || (d.equals(max) && d.equals(smax.toDouble)) ){
            tuple=new Tuple2[String,Int](smin.toString()+"-"+smax.toString(),1)
            loop2.break()
          }
        }
      }
      tuple
    }).reduceByKey((d1,d2)=>d1+d2)



    var rectangleLabelStr=splitList.map(m=>m.get("small").get+"-"+m.get("big").get).reduce((a1,a2)=>a1+","+a2)

    var rectangleMap=rectangleTs.collectAsMap()
    var rectangleDataStr=splitList.map(m=>{
      var value=rectangleMap.get(m.get("small").get+"-"+m.get("big").get)
      if(value.isEmpty) "0" else value.get.toString
    } ).reduce((a,b)=>a + "," + b)

    println("============== kernal data:")
    println("kernalLebelStr  ===>"+kernalLebelStr)
    println("densitiesStr  ===>"+densitiesStr)
    println("============== rectangle data:")
    println("rectangleLabelStr  ===>"+rectangleLabelStr)
    println("rectangleDataStr  ===>"+rectangleDataStr)
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

}
