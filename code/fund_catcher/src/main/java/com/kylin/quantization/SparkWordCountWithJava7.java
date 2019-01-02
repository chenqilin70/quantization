package com.kylin.quantization;

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
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class SparkWordCountWithJava7 implements Serializable{
    private static final long serialVersionUID = 4125096758372084309L;


    public static  void main(String[] args) throws IOException {
        JavaSparkContext context = getSparkContext();
        Configuration hconf = getHconf();
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        List<Tuple2<String, Date>> collect = hbaseRdd.flatMapToPair(new PairFlatMapFunction<Tuple2<ImmutableBytesWritable, Result>, String, Date>() {

            @Override
            public Iterable<Tuple2<String, Date>> call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                System.out.println("_1:::" + Bytes.toString(tuple._1.get()));
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
            System.out.println(t._1+":::::"+t._2.toLocaleString());

        }


        context.stop();
    }



    public static JavaSparkContext getSparkContext(){
        SparkConf conf = new SparkConf().setAppName("Spark");
            /*独立模式
            conf.setMaster("spark://master56:7077");
            conf.set("spark.cores.max", "48");
            */
             /*yarn-client模式*/
        conf.setMaster("yarn-client");
        //设置程序包
        conf.setJars(new String[]{"/usr/local/workspace/wc/fund_catcher-1.0-SNAPSHOT.jar"});
        //设置SparkHOME
        conf.setSparkHome("/opt/cloudera/parcels/CDH-5.13.3-1.cdh5.13.3.p0.2/lib/spark");
        //设置运行资源参数
        conf.set("spark.executor.instances", "30");
        conf.set("spark.executor.cores", "3");
        conf.set("spark.executor.memory", "500M");
        conf.set("spark.driver.memory", "300M");
        conf.set("spark.driver.maxResultSize", "1G");
        JavaSparkContext context = new JavaSparkContext(conf);
        return context;
    }
    public static Configuration getHconf() throws IOException {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "netval";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("161604"));
        Scan scan = new Scan().setFilter(filter);
        hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        return hconf;
    }

    public static String convertScanToString(Scan scan) throws IOException {
        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        return Bytes.toString(java.util.Base64.getEncoder().encode(proto.toByteArray()));
    }


}
