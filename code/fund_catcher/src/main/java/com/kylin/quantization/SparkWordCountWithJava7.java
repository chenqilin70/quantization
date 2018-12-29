package com.kylin.quantization;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: SparkWordCountWithJava7
 * Description:
 * Author: aierxuan
 * Date: 2018-12-28 14:05
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class SparkWordCountWithJava7 {
    public static  void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("Spark");
            /*独立模式
            conf.setMaster("spark://master56:7077");
            conf.set("spark.cores.max", "48");
            */
             /*yarn-client模式*/
        conf.setMaster("yarn-client");
        //设置程序包
        conf.setJars(new String[]{"/usr/local/workspace/wc/wc.jar"});
        //设置SparkHOME
        conf.setSparkHome("/opt/cloudera/parcels/CDH-5.13.3-1.cdh5.13.3.p0.2/lib/spark");
        //设置运行资源参数
        conf.set("spark.executor.instances", "30");
        conf.set("spark.executor.cores", "3");
        conf.set("spark.executor.memory", "500M");
        conf.set("spark.driver.memory", "300M");
        conf.set("spark.driver.maxResultSize", "1G");
        JavaSparkContext context = new JavaSparkContext(conf);
        //设置运行资源参数
        JavaRDD<String> rdd = context.textFile("hdfs:///workspace/wc/1.txt");
        List<Tuple2<String, Integer>> result = rdd.flatMapToPair(new PairFlatMapFunction<String, String, Integer>() {
            @Override
            public Iterable<Tuple2<String, Integer>> call(String arg0)
                    throws Exception {
                ArrayList<Tuple2<String, Integer>> list = new ArrayList<Tuple2<String, Integer>>();
                String[] array = arg0.split(",");
                for (String temper : array) {
                    list.add(new Tuple2<String, Integer>(temper, 1));
                }
                return list;
            }

        }).reduceByKey(new Function2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer arg0, Integer arg1)
                    throws Exception {
                return arg0 + arg1;
            }

        }).collect();
        //打印结果
        for (Tuple2<String, Integer> temper : result) {
            System.out.println("================================>"+temper._1 + "," + temper._2);
        }
        context.stop();
    }
}
