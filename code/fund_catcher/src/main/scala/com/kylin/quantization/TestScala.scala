package com.kylin.quantization

import com.kylin.quantization.util.JedisUtil.JedisRunner
import com.kylin.quantization.util.{JedisUtil, RowKeyUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.{DataFrame, SQLContext}
import redis.clients.jedis.Jedis
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s._

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
    var test=("asdf"->"sdfsdf")
    var m1=("111"->List(1,2)) ~ ("222"->"eee")
    var m2=("111"->List(1,2)) ~ ("222"->"eee")

    println(compact(render(List(m1,m2))))
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    null
  }
}
