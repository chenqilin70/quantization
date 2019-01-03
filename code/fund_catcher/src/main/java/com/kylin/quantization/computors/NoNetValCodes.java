package com.kylin.quantization.computors;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
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
    /*public static HBaseDao hBaseDao=new HBaseDaoImpl();
    static {
        hBaseDao.setHconfiguration(new CatcherConfig().hconfiguration());
    }*/
    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        Configuration hconf =getNetValHconf("161604");
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        List<Tuple2<String, String>> collect = hbaseRdd.mapToPair(new PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, String>() {
            @Override
            public Tuple2<String, String> call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {

                byte[] ljjz = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("LJJZ"));
                String ljjzStr = Bytes.toString(ljjz);


                /*byte[] fundcodeArr = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("fundcode"));
                byte[] jjqcArr = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("jjqc"));
                String fundcode = Bytes.toString(fundcodeArr);
                String jjqc = Bytes.toString(jjqcArr);*/
            /*Filter netvalFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("_" + fundcode + "_"));
            Scan netvalScan = new Scan().setFilter(netvalFilter);
            String result = hBaseDao.table("netval", nvtable -> {
                ResultScanner nvScanner = nvtable.getScanner(netvalScan);
                Result next = nvScanner.next();
                boolean flag = next == null;
                nvScanner.close();
                return flag ? fundcode+"("+jjqc+")" : "";
            });*/

                return new Tuple2<String, String>(Bytes.toString(tuple._1.get()), ljjzStr);
            }
        })/*.map(new Function<Tuple2<String, String>, String>() {
            @Override
            public String call(Tuple2<String, String> tuple) throws Exception {
                System.out.println("sdfdsfdsf");
                String fundcode = tuple._1;
                String jjqc = tuple._2;
                Configuration netValHconf = getNetValHconf(fundcode);
                JavaSparkContext netvalcontext = new JavaSparkContext(sparkConf());
                JavaPairRDD<ImmutableBytesWritable, Result> netvalbaseRdd = netvalcontext.newAPIHadoopRDD(netValHconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
                List<Integer> nets = netvalbaseRdd.map(t -> 1).collect();
                netvalcontext.close();
                if (nets.size() == 0) {
                    return fundcode + "(" + jjqc + ")";
                }
                return "";
            }
        })*/.collect();
       /* collect.remove("");*/
        collect.forEach(c->{
            System.out.println(c._1()+","+c._2);
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
        Filter pageFilter=new PageFilter(10);
        FilterList allList=new FilterList(FilterList.Operator.MUST_PASS_ALL,conditionList,pageFilter);
        scan.setFilter(allList);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }


    private static Configuration getNetValHconf(String fundcode)  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "netval";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter netvalFilter=new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("_"+fundcode+"_"));
        Filter pageFilter=new PageFilter(2);
        Scan netvalScan=new Scan().setFilter(new FilterList(FilterList.Operator.MUST_PASS_ALL,pageFilter,netvalFilter));
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(netvalScan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }




}
