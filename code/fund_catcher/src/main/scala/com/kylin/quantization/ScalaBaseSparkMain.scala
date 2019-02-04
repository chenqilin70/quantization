package com.kylin.quantization



import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.config.CatcherConfig
import com.kylin.quantization.util.SqlConfigUtil
import org.apache.commons.lang3.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext

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

  def  sql(sqlTab :String,sparkContext: SparkContext,sqlSparkContext : SQLContext):RDD[BigDecimal]={
    var sql=SqlConfigUtil.getBizSql(sqlTab,SqlConfigUtil.SPARK_DOC);
    var regist=SqlConfigUtil.attr(sqlTab,"regist",SqlConfigUtil.SPARK_DOC);
    if(StringUtils.isNotBlank(regist)){
      for(r<-regist.split(",")){
        BaseSparkMain.registerHbaseTable(r,sparkContext,sqlSparkContext)
      }
    }

    var df=sqlSparkContext.sql(sql)
    df.show()
    return null;


  }





}
