package com.kylin.quantization

import java.util.HashMap

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.dao.impl.HiveDaoImpl
import com.kylin.quantization.model.{Kzzmx, LoadDataModel, Workedkzzmx}
import com.kylin.quantization.util.{HdfsUtil, JedisUtil, MapUtil}
import org.apache.calcite.avatica.Meta.Pat
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkContext
import org.apache.spark.mllib.random.RandomRDDs._
import org.apache.spark.sql.{SQLContext, SaveMode}
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
      if(kzzmx.getNowyear.toInt>RATEDESList.size){
        nowyearRate=BigDecimal(0)
      }else{
        nowyearRate=RATEDESList.apply(if(kzzmx.getNowyear.toInt==0) 0 else (kzzmx.getNowyear.toInt-1))
      }


      var daoqijiazhi=BigDecimal(0)
      for(i<-Range(if(kzzmx.getNowyear.toInt==0) 0 else (kzzmx.getNowyear.toInt-1),RATEDESList.size,1)){
        daoqijiazhi=daoqijiazhi.+(RATEDESList.apply(i))
      }
      kzzmx.setDaoQiJiaZhi(daoqijiazhi.+(100).toString())
      kzzmx.setHuiShouJia(nowyearRate.+(100).toString())
      kzzmx
    })
    var worked_kzzmx=sqlSc.createDataFrame(javaRdds,classOf[Workedkzzmx])
    BaseSparkMain.registerHbaseTable("worked_kzzmx",worked_kzzmx)


    var df2=sql("kzz_select2",sc,sqlSc)
    var destPath="hdfs://nameservice1/workspace/externalData/workedkzz"
    var dao=new HiveDaoImpl()
    dao.executeSql("create_worked_kzz",false)
    HdfsUtil.getFs.delete(new Path("hdfs://nameservice1/workspace/externalData/workedkzz/*"),true)
    df2.write.format("parquet").mode(SaveMode.Overwrite).save(destPath)
    var load=new LoadDataModel(destPath+"/*.parquet","fund_catcher.workedkzz").setLocal(LoadDataModel.HDFS_FILE).setOverwrite(LoadDataModel.OVERWRITE_TABLE)
    dao.loadData(load)
    sc.stop()
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {null}
}
