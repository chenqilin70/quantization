package com.kylin.quantization

import com.kylin.quantization.KernalMain.{SQL_TAB, sparkConf, sql}
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext

object MovingAveragesMain extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
    var sparkContext =new SparkContext(sparkConf())
    var sqlContext=new HiveContext(sparkContext)

    var df=sql("movingAverages",sparkContext,sqlContext)
    df.rdd.collect().foreach(r=>{
      println(r.toString())
    })

  }

}
