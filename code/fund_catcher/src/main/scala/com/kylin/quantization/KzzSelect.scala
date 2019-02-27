package com.kylin.quantization

import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.model.{Kzzmx, Workedkzzmx}
import com.kylin.quantization.util.JedisUtil
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import org.apache.spark.mllib.random.RandomRDDs._
import org.apache.spark.sql.SQLContext
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.util.matching.Regex


object KzzSelect extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
    var sc=new SparkContext(sparkConf())
    var sqlSc=new SQLContext(sc)
    var df=sql("kzz_select1",sc,sqlSc)
    df.show(20)
    var pattern =new Regex("\\d{1,}\\.{1}\\d{1,}")
    var javaRdds=df.map(d=>{
      var kzzmx=new Workedkzzmx()
      kzzmx.setRowkey(d.getString(0))
      kzzmx.setBONDCODE(d.getString(1))
      kzzmx.setRATEDES(d.getString(2))
      kzzmx.setMRTYDATE(d.getString(3))
      kzzmx.setNowyear(d.getString(4))
      kzzmx.setFRSTVALUEDATE(d.getString(5))
      var RATEDESList=(pattern findAllIn d.getString(2)).map(i=>BigDecimal(i)).toList
      var nowyearRate:BigDecimal=null
      if(kzzmx.getNowyear.toInt>RATEDESList.size-1){
        nowyearRate=BigDecimal(0)
      }else{
        nowyearRate=RATEDESList.apply(kzzmx.getNowyear.toInt)
      }


      var daoqijiazhi=BigDecimal(0)
      for(i<-Range(kzzmx.getNowyear.toInt,RATEDESList.size,1)){
        daoqijiazhi=daoqijiazhi.+(RATEDESList.apply(i))
      }
      kzzmx.setDaoQiJiaZhi(daoqijiazhi.+(100).toString())
      kzzmx.setHuiShouJia(nowyearRate.+(100).toString())
      kzzmx
    })
    var worked_kzzmx=sqlSc.createDataFrame(javaRdds,classOf[Workedkzzmx])
    BaseSparkMain.registerHbaseTable("worked_kzzmx",worked_kzzmx)


    var df2=sql("kzz_select2",sc,sqlSc)
    df2.collect().foreach(r=>{
      for(i<-Range(0,r.size)){
        print(r.get(i)+"\t")
      }
      println("")
    })
    sc.stop()
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {null}
}
