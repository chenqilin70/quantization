package com.kylin.quantization.util

import java.io.IOException

import com.kylin.quantization.computors.BaseSparkMain
import com.kylin.quantization.config.CatcherConfig
import com.kylin.quantization.dao.impl.{HBaseDaoImpl, HBaseExecutors}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Admin, Result, Scan, Table}
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes

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
          println("======================="+tableName)
          dao.scanForEach(tableName,scan,new HBaseExecutors.ScanForEachExecutor {
            override def doEach(result: Result): Unit = {
              var keyset=result.getMap.keySet()
              var it=keyset.iterator()
              while(it.hasNext){
                println(Bytes.toString(it.next()))
              }
            }
          })
        })
        return null;
      }
    })
  }

}

