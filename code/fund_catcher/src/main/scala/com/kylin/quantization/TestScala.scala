package com.kylin.quantization

import java.util.regex.{Matcher, Pattern}

import com.alibaba.fastjson.JSONObject
import com.kylin.quantization.util.JedisUtil.JedisRunner
import com.kylin.quantization.util.{JedisUtil, RowKeyUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.{DataFrame, SQLContext}
import redis.clients.jedis.Jedis
import org.json4s.JsonDSL._
import org.json4s.NoTypeHints
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import org.json4s.jackson.Serialization

import scala.util.matching.Regex

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
//    var str="第一年到第五年的利率分别为:第一年0.8%、第二年1.0%、第三年1.2%、第四年1.6%、第五年2.0%。"
//    var pattern =new Regex("\\d{1,}\\.{1}\\d{1,}")
//    println(  (pattern findAllIn str).mkString(",")   )
    for(i <-Range(8,4,1)){
      println(i)
    }

  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    null
  }
}
