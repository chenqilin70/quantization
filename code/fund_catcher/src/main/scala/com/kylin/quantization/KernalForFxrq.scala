package com.kylin.quantization

import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

/**
  * ClassName: KernalForFxrq
  * Description:
  * Author: aierxuan
  * Date: 2019-02-04 11:06
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
object KernalForFxrq extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
    var sparkContext =new SparkContext(sparkConf())
    var sqlContext=new SQLContext(sparkContext)
    sql("test",sparkContext,sqlContext)
  }

}
