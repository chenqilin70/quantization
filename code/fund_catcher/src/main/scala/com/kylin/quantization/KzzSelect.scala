package com.kylin.quantization

import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.model.Kzzmx
import com.kylin.quantization.util.JedisUtil
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import org.apache.spark.mllib.random.RandomRDDs._
import org.apache.spark.sql.SQLContext
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._


object KzzSelect extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
    var sc=new SparkContext(sparkConf())
    var sqlSc=new SQLContext(sc)
    var df=sql("kzz_select1",sc,sqlSc)
    df.show(20)
    var javaRdds=df.map(d=>{
      var kzzmx=new Kzzmx()
      kzzmx.setRowkey(d.getString(0))
      kzzmx.setBONDCODE(d.getString(1))
      kzzmx.setRATEDES(d.getString(2))
      kzzmx.setMRTYDATE(d.getString(3))
      kzzmx
    })
    var worked_kzzmx=sqlSc.createDataFrame(javaRdds,classOf[Kzzmx])
    BaseSparkMain.registerHbaseTable("worked_kzzmx",worked_kzzmx)


    var df2=sql("kzz_select2",sc,sqlSc)
    df2.show(20)
    sc.stop()
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {null}
}
