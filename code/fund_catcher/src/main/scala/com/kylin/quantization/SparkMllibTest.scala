package com.kylin.quantization
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.kylin.quantization.util.JedisUtil
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.{Matrix, Vector, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.stat.test.ChiSqTestResult
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkContext
import org.apache.spark.mllib.random.RandomRDDs._



object SparkMllibTest extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
    var sc=new SparkContext(sparkConf())
    val u = normalRDD(sc, 10000L, 10)//生成100个服从标准正态分面N(0,1)的随机RDD数据，10为指定的分区数

    val v = u.map(x => 1.0 + 2.0 * x)//转换使其服从N(1,4)的正太分布


    var result=List[Map[String,Object]]();


    var ucollect=u.collect()
    var ulist=List[List[Any]]()
    for(i <- Range(0,ucollect.size)){
      ulist=ulist :+ List(i,ucollect(i))
    }
    var map1=Map("type" -> "line","data" -> ulist)

    var vcollect=v.collect()
    var vlist=List[List[Any]]()
    for(i <- Range(0,vcollect.size)){
      vlist=vlist :+ List(i,vcollect(i))
    }
    var map2=Map("type" -> "line","data" -> vlist)




    result=result :+ (map1)
    result=result :+ (map2)
    println(result)
    println("=========================================")
    var resultJson=JSON.toJSONString(result,SerializerFeature.WriteMapNullValue)

    println(resultJson)
    JedisUtil.set("lineSeries",resultJson)
    JedisUtil.destroy()
    sc.stop()
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {null}
}
