package com.kylin.quantization
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
object SparkMllibTest extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
    var sparkContext=new SparkContext(sparkConf())
    var sqlSparkContext=new SQLContext(sparkContext)
    var df=sql("kernal_list",sparkContext,sqlSparkContext)

    val observations: RDD[Vector] = df.rdd.map(m=>Vectors.dense(m.getDecimal(0).doubleValue()))
    val summary: MultivariateStatisticalSummary = Statistics.colStats(observations)

    /**
      * 最大值、最小值、平均值、方差、非零元素的数量以及总数
      */
    println("summary.max"+summary.max)
    println("summary.min"+summary.min)
    println("summary.mean"+summary.mean) //每个列值组成的密集向量
    println("summary.variance"+summary.variance) //列向量方差
    println("summary.numNonzeros"+summary.numNonzeros) //每个列的非零值个数
    println("summary.count"+summary.count)


    sparkContext.stop()
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {null}
}
