package com.kylin.quantization

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
//    var json=new JSONObject()
//    json.put("aaaa",List(1,2,3))
//    println(json.toJSONString)


    implicit val formats = Serialization.formats(NoTypeHints)
    val m = Map(
      "name" -> "john doe",
      "age" -> 18,
      "hasChild" -> true,
      "childs" -> List(
        Map("name" -> "dorothy", "age" -> 5, "hasChild" -> List(1,2,3,4,5)),
        Map("name" -> "bill", "age" -> 8, "hasChild" -> false)))

    val mm = Map(
      "1" -> Map ("1"->"1.2")
    )

    println(write(List(m,mm)))
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    null
  }
}
