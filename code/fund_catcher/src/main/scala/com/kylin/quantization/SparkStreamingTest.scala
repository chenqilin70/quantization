package com.kylin.quantization
import com.kylin.quantization.util.JedisUtil
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Durations, StreamingContext}

object SparkStreamingTest  extends ScalaBaseSparkMain{
  override def getCustomHbaseConf(): Map[String, Configuration] ={null}

  def main(args: Array[String]): Unit = {
    var sparkContext=new SparkContext(sparkConf())
    var streamContext=new StreamingContext(sparkContext,Durations.seconds(5))
    var dstream=streamContext.socketTextStream("node4",9999);
    var result=dstream.map(s=>s+"hhh").reduce((s1,s2)=>s1+"-"+s2).foreachRDD(r=>r.foreach(v=>JedisUtil.set("stream",v)))


    streamContext.start()
    streamContext.awaitTermination()
    streamContext.stop(false)
  }
}
