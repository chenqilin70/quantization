package com.kylin.quantization

import com.kylin.quantization.util.RowKeyUtil
import org.apache.hadoop.conf.Configuration
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * ClassName: TestScala
  * Description:
  * Author: aierxuan
  * Date: 2019-02-15 13:40
  * History:
  * <author> <time> <version>    <desc>
  * 作者姓名 修改时间    版本号 描述
  */
object TestScala  extends ScalaBaseSparkMain{
  def main(args: Array[String]): Unit = {
    val sparkContext = new JavaSparkContext(sparkConf())
    val sqlContext = new SQLContext(sparkContext)
    var df=sql("test",sparkContext,sqlContext )
    df.show()
    df.show(100)
    sparkContext.stop()
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    null
  }
}
