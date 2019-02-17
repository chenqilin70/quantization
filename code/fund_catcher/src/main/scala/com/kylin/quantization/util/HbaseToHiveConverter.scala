package com.kylin.quantization.util

import java.io.IOException

import com.kylin.quantization.CatcherMain
import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.config.CatcherConfig
import com.kylin.quantization.dao.impl.{HBaseDaoImpl, HBaseExecutors}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Admin, Result, Scan, Table}
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.log4j.PropertyConfigurator
import org.slf4j.LoggerFactory

/**
  * ClassName: HbaseToHiveConverter
  * Description:
  * Author: aierxuan
  * Date: 2019-02-16 18:21
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
object HbaseToHiveConverter {
  def main(args: Array[String]): Unit = {
    convert()
  }
  def convert(): Unit ={
    var dao=new HBaseDaoImpl()
    dao.setHconfiguration(new CatcherConfig().hconfiguration());
    dao.admin(new HBaseExecutors.AdminExecutor[Object] {
      override def doAdmin(admin: Admin): Object = {
        admin.listTables().foreach(tdesc=>{
          var tableName=tdesc.getTableName.toString
          var scan=new Scan()
          var filter=new PageFilter(1);
          scan.setFilter(filter)
          dao.scanForEach(tableName,scan,new HBaseExecutors.ScanForEachExecutor {
            override def doEach(result: Result): Unit = {
              var fields=new StringBuffer("")
              var qulifier=new StringBuffer("")
              var familyMap=result.getFamilyMap(Bytes.toBytes("baseinfo"));
              var it=familyMap.keySet().iterator();
              while(it.hasNext){
                var f=Bytes.toString(it.next())
                fields.append(f+" string,")
                qulifier.append("baseinfo:"+f+",")
              }
              val fieldsStr=fields.substring(0,fields.length()-1)
              val qulifierStr=qulifier.substring(0,qulifier.length()-1)
              var sql=SqlConfigUtil.getBizSql("create_table_tamplate",SqlConfigUtil.HIVE_DOC)
              sql=StringReplaceUtil.replace(sql,new MapUtil[String,String]().create("fields",fieldsStr,"qulifier",qulifierStr,"tableName",tableName))
              println(sql)
              println("========================")

            }
          })
        })
        return null;
      }
    })
  }




}

