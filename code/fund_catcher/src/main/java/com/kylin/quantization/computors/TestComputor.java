package com.kylin.quantization.computors;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

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
        List<Tuple2<String ,String>> data = Arrays.asList(new Tuple2<String ,String>("a","1"),new Tuple2<String ,String>("b","2")
                ,new Tuple2<String ,String>("c","3"),new Tuple2<String ,String>("d","4"),new Tuple2<String ,String>("e","5"));
        JavaPairRDD<String, String> rdd1 = context.parallelizePairs(data);


        List<Tuple2<String ,String>> data1 = Arrays.asList(new Tuple2<String ,String>("a","aa"),new Tuple2<String ,String>("b","bb")
               ,new Tuple2<String ,String>("d","dd"),new Tuple2<String ,String>("e","ee"));
        JavaPairRDD<String, String> rdd2 = context.parallelizePairs(data1);
        JavaPairRDD<String, Tuple2<String, String>> join = rdd1.join(rdd2);
        List<Tuple2<String, Tuple2<String, String>>> collect = join.collect();
        for(Tuple2<String, Tuple2<String, String>> t :collect){
            System.out.print(t._1+"==");
            System.out.print(t._2._1+"==");
            System.out.println(t._2._2);

        }



    }
}
