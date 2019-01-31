package com.kylin.quantization

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
    val site: List[Double] = List()

    print(site)

/*
    val sparkContext=new SparkContext(sparkConf());
    val sample=sparkContext.parallelize(Seq(0.0,1.0,4.0,4.0))

    val kd=new KernelDensity()
      .setSample(sample)
      .setBandwidth(3.0)
    val site: List[Double] = List()
    for(i<-Range(-10,60,1)){
      val i1=i.toDouble/10
      println(i1)

    }
    val densities = kd.estimate(Array(1.0, 2.0, 4.0))
    for( d <- densities){
      println(d+","+d.getClass)
    }*/

  }

}






