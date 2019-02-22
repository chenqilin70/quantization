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
    var df=sql("mltest",sparkContext,sqlSparkContext)

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



    val data =df.rdd.map(r=>new Tuple2(   if(r.getDecimal(0).doubleValue()>4.3)"长" else "短"   ,r.getString(1)))
    val fractions: Map[String , Double] =Map("长"->0.4,"短"->0.6)
    val exactSample = data.sampleByKey(withReplacement = true, fractions,0)
    exactSample.collect().foreach((cd)=>{
      println(cd._1+":"+cd._2)
    })


    var cdatacount=data.filter(s=>s._1.equals("长")).count()
    var ddatacount=data.filter(s=>s._1.equals("短")).count()
    println("cdatacount :"+cdatacount)
    println("ddatacount :"+ddatacount)

    var ccount=exactSample.filter(s=>s._1.equals("长")).count()
    var dcount=exactSample.filter(s=>s._1.equals("短")).count()
    println("chang :"+ccount)
    println("duan :"+dcount)




    sparkContext.stop()
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {null}
}
