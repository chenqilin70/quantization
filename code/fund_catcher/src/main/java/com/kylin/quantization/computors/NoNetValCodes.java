package com.kylin.quantization.computors;

import com.google.common.base.Optional;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ClassName: NoNetValCodes
 * Description:
 * Author: aierxuan
 * Date: 2019-01-03 10:42
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class NoNetValCodes extends  BaseSparkMain{
    public static HBaseDao hBaseDao=new HBaseDaoImpl();
    static {
        System.out.println("00000000000000000000000000000000000000");
        hBaseDao.setHconfiguration(new CatcherConfig().hconfiguration());
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }
    private static JavaSparkContext  context = new JavaSparkContext(sparkConf());
    public static void main(String[] args) {
        System.out.println("1111111111111111111111111111111111111");
        Configuration hconf =getFundListHconf();
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        JavaPairRDD<String, String> fundRdd = hbaseRdd.mapToPair(new PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, String>() {
            @Override
            public Tuple2<String, String> call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {

                byte[] fundcodeArr = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("fundcode"));
                byte[] jjqcArr = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("jjqc"));
                String fundcode = Bytes.toString(fundcodeArr);
                String jjqc = Bytes.toString(jjqcArr);
                return new Tuple2<String, String>(fundcode, jjqc);
            }
        });
        JavaPairRDD<ImmutableBytesWritable, Result> netvalbaseRdd = context.newAPIHadoopRDD(getNetValHconf(), TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        JavaPairRDD<String, String> netvalRdd = netvalbaseRdd.mapToPair(new PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, String>() {
            @Override
            public Tuple2<String, String> call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                String rowkey = Bytes.toString(tuple._1().get());
                String fundcode = rowkey.substring(rowkey.indexOf("_") + 1, rowkey.lastIndexOf("_"));
                return new Tuple2<String, String>(fundcode, "1");
            }
        });
        JavaPairRDD<String, Tuple2<String, Optional<String>>> joinRdd = fundRdd.leftOuterJoin(netvalRdd);
        List<Tuple2<String, Tuple2<String, Optional<String>>>> collect = joinRdd.collect();
        collect.forEach(c->{
            System.out.println(c._1+":"+c._2._1+":"+c._2._2.get());
        });



        context.close();
    }

    private static void sleep() {
        for(int j=0;j<100;j++){
            int i = new Random().nextInt(1000)+300;
            System.out.println(i);
        }
    }


    private static Configuration getFundListHconf()  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "fund";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Scan scan=new Scan();
        Filter fundcodeFilter=new QualifierFilter(CompareFilter.CompareOp.EQUAL,new BinaryComparator(Bytes.toBytes("fundcode")));
        Filter jjqcFilter=new QualifierFilter(CompareFilter.CompareOp.EQUAL,new BinaryComparator(Bytes.toBytes("jjqc")));
        FilterList conditionList=new FilterList(FilterList.Operator.MUST_PASS_ONE,fundcodeFilter,jjqcFilter);
//        Filter pageFilter=new PageFilter(10);
//        FilterList allList=new FilterList(FilterList.Operator.MUST_PASS_ALL,conditionList,pageFilter);
        scan.setFilter(conditionList);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }


    private static Configuration getNetValHconf()  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "netval";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Scan netvalScan=new Scan();
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(netvalScan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }




}
