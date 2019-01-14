package com.kylin.quantization.computors;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: TestComputor
 * Description:
 * Author: aierxuan
 * Date: 2019-01-11 16:54
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class TestComputor  extends BaseSparkMain{
    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        List<Tuple2<String ,String>> data = new ArrayList<>();
        JavaPairRDD<String, String> rdd1 = context.parallelizePairs(data);
        rdd1.map(r->1l).reduce((r1,r2)->r1+r2);



    }
}
