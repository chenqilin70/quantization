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
        hBaseDao.setHconfiguration(new CatcherConfig().hconfiguration());
    }
    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        Configuration hconf =getFundListHconf();
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        List<String> collect = hbaseRdd.map(tuple -> {
//            sleep();
            byte[] fundcodeArr = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("fundcode"));
            byte[] jjqcArr = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("jjqc"));
            String fundcode=Bytes.toString(fundcodeArr);
            String jjqc=Bytes.toString(jjqcArr);
            Filter netvalFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("_" + fundcode + "_"));
            Scan netvalScan = new Scan().setFilter(netvalFilter);
            String result = hBaseDao.table("netval", nvtable -> {
                ResultScanner nvScanner = nvtable.getScanner(netvalScan);
                Result next = nvScanner.next();
                boolean flag = next == null;
                nvScanner.close();
                return flag ? fundcode+"("+jjqc+")" : "";
            });
            /*Configuration netValHconf = getNetValHconf(Bytes.toString(fundcodeArr));
            JavaPairRDD<ImmutableBytesWritable, Result> netvalbaseRdd = context.newAPIHadoopRDD(netValHconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
            List<Integer> collect = netvalbaseRdd.map(t -> 1).collect();
            collect.size()*/
            return result;
        }).collect();
        collect.remove("");
        collect.forEach(c->{
            System.out.println(c);
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
        FilterList filterList=new FilterList(FilterList.Operator.MUST_PASS_ONE,fundcodeFilter,jjqcFilter);
        scan.setFilter(filterList);
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
        Scan netvalScan=new Scan().setFilter(netvalFilter);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(netvalScan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }




}
