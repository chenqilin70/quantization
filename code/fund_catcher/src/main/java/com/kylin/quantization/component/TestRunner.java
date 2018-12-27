package com.kylin.quantization.component;

import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.util.RowKeyUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ClassName: TestRunner
 * Description:
 * Author: aierxuan
 * Date: 2018-12-23 9:48
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Component
public class TestRunner  implements ApplicationRunner {
    public static Logger logger= Logger.getLogger(TestRunner.class);
    @Autowired
    private HBaseDao hBaseDao;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        new SparkWordCountWithJava7().test();

    }




    public static class SparkWordCountWithJava7 {
        private static final Pattern SPACE = Pattern.compile(",");

        public static void test() throws Exception {
            SparkConf conf = new SparkConf().setAppName("JavaWordCount");
            conf.setMaster("spark://192.168.109.205:7077");
            JavaSparkContext sc = new JavaSparkContext(conf);
            JavaRDD<String> lines = sc.textFile("/usr/local/workspace/wc",1);
            JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Iterable<String> call(String s) {
                    return Arrays.asList(SPACE.split(s));
                }
            });

            JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Tuple2<String, Integer> call(String s) {
                    return new Tuple2<String, Integer>(s, 1);
                }
            });

            JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Integer call(Integer i1, Integer i2) {
                    return i1 + i2;
                }
            });

            List<Tuple2<String, Integer>> output = counts.collect();
            for (Tuple2<?, ?> tuple : output) {
                System.out.println(tuple._1() + ": " + tuple._2());
            }

            sc.stop();
        }
    }
}
