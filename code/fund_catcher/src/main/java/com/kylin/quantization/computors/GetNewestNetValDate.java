package com.kylin.quantization.computors;

import com.kylin.quantization.config.CatcherConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ClassName: SparkWordCountWithJava7
 * Description:
 * Author: aierxuan
 * Date: 2018-12-28 14:05
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class GetNewestNetValDate implements Serializable{
    public static Logger logger = Logger.getLogger(GetNewestNetValDate.class);


    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        Configuration hconf = getMaxNetValHconf("161604");
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        List<Tuple2<String, Date>> collect = hbaseRdd.flatMapToPair(new PairFlatMapFunction<Tuple2<ImmutableBytesWritable, Result>, String, Date>() {

            @Override
            public Iterable<Tuple2<String, Date>> call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                List<Tuple2<String, Date>> result = new ArrayList<>();
                byte[] o = tuple._2().getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("FSRQ"));
                if (o != null) {
                    String date = Bytes.toString(o);
                    String code=Bytes.toString(tuple._1.get());
                    code=code.substring(code.indexOf("_"));
                    code=code.substring(0,code.lastIndexOf("_"));
                    code=code.replaceAll("_","");
                    result.add(new Tuple2<String, Date>(code, new SimpleDateFormat("yyyy-MM-dd").parse(date)));
                }
                return result;
            }
        }).reduceByKey(new Function2<Date, Date, Date>() {
            @Override
            public Date call(Date date, Date date2) throws Exception {
                if (date.getTime() > date2.getTime()) {
                    return date;
                } else {
                    return date2;
                }
            }
        }).collect();
        for(Tuple2<String, Date> t: collect){
            logger.info(t._1+":::::"+t._2.toLocaleString());

        }


        context.stop();
    }



    private static   Configuration getMaxNetValHconf(String code)  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "netval";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("_"+code+"_"));
        Scan scan = new Scan().setFilter(filter);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }


    public static SparkConf sparkConf(){
        Map<String, String> sparkMap = CatcherConfig.proToMap("spark.properties");
        SparkConf conf = new SparkConf().setAppName(sparkMap.get("spark.appName"));
        /*yarn-client模式*/
        conf.setMaster(sparkMap.get("spark.master"));
        //设置程序包
        conf.setJars(new String[]{sparkMap.get("spark.jar")});
        //设置SparkHOME
        conf.setSparkHome(sparkMap.get("spark.sparkhome"));
        //设置运行资源参数
        conf.set("spark.executor.instances", sparkMap.get("spark.executor.instances"));
        conf.set("spark.executor.cores", sparkMap.get("spark.executor.cores"));
        conf.set("spark.executor.memory", sparkMap.get("spark.executor.memory"));
        conf.set("spark.driver.memory", sparkMap.get("spark.driver.memory"));
        conf.set("spark.driver.maxResultSize", sparkMap.get("spark.driver.maxResultSize"));
        return conf;
    }

    public static String convertScanToString(Scan scan) throws IOException {
        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        return Bytes.toString(java.util.Base64.getEncoder().encode(proto.toByteArray()));
    }


}
