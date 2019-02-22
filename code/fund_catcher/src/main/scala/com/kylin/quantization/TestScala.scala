package com.kylin.quantization

import com.kylin.quantization.util.JedisUtil.JedisRunner
import com.kylin.quantization.util.{JedisUtil, RowKeyUtil}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.{DataFrame, SQLContext}
import redis.clients.jedis.Jedis

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

    val v1 = Vectors.dense(1.0,2.0 ,3.0 ,4.0 ,5.0 )

    val v2 = Vectors.dense(44.0, 4.0)

    val c1 = Statistics.chiSqTest(v1)
    /**
      * method: 方法。这里采用pearson方法。

        statistic：
          检验统计量。简单来说就是用来决定是否可以拒绝原假设的证据。检验统计量的值是利用样本数据计算得到的，
          它代表了样本中的信息。检验统计量的绝对值越大，拒绝原假设的理由越充分，反之，不拒绝原假设的理由越充分。

        degrees of freedom：
          自由度。表示可自由变动的样本观测值的数目，

        pValue：
          统计学根据显著性检验方法所得到的P 值。一般以P < 0.05 为显著， P<0.01 为非常显著，
          其含义是样本间的差异由抽样误差所致的概率小于0.05 或0.01。

        一般来说，假设检验主要看P值就够了。
      */
    println(c1)
  }

  override def getCustomHbaseConf(): Map[String, Configuration] = {
    null
  }
}
