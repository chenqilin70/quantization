package com.kylin.quantization



import com.kylin.quantization.config.CatcherConfig
import org.apache.spark.SparkConf

/**
  * ClassName: ScalaBaseSparkMain
  * Description:
  * Author: aierxuan
  * Date: 2019-01-31 13:41
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
class ScalaBaseSparkMain {
  def sparkConf(): SparkConf = {
    val sparkMap = CatcherConfig.proToMap("spark.properties")
    val conf = new SparkConf().setAppName(sparkMap.get("spark.appName"))
    conf.setMaster(sparkMap.get("spark.master"))
    //设置SparkHOME
    conf.setSparkHome(sparkMap.get("spark.sparkhome"))
    //设置运行资源参数
    conf.set("spark.executor.instances", sparkMap.get("spark.executor.instances"))
    conf.set("spark.executor.cores", sparkMap.get("spark.executor.cores"))
    conf.set("spark.executor.memory", sparkMap.get("spark.executor.memory"))
    conf.set("spark.driver.memory", sparkMap.get("spark.driver.memory"))
    conf.set("spark.driver.maxResultSize", sparkMap.get("spark.driver.maxResultSize"))
    conf
  }





}
