package com.kylin.quantization

import java.io.ByteArrayInputStream
import java.util
import java.util.regex.{Matcher, Pattern}
import java.util.HashMap

import com.alibaba.fastjson.serializer.{SerializeFilter, SerializerFeature}
import com.alibaba.fastjson.{JSON, JSONObject}
import com.kylin.quantization.dao.impl.HiveDaoImpl
import com.kylin.quantization.model.LoadDataModel
import com.kylin.quantization.util.JedisUtil.JedisRunner
import com.kylin.quantization.util.{HdfsUtil, JedisUtil, RowKeyUtil}
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

  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    null
  }
}
