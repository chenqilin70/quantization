package com.kylin.quantization

import com.kylin.quantization.util.JedisUtil.JedisRunner
import com.kylin.quantization.util.{JedisUtil, RowKeyUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.{DataFrame, SQLContext}
import redis.clients.jedis.Jedis

/**
  * ClassName: TestScala
  * Description:
  * Author: aierxuan
  * Date: 2019-02-15 13:40
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
object TestScala  extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
  var list1=List[Int]()
    var aaa=list1 :+ List(5)
    println(list1)
    println(aaa)
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    null
  }
}
