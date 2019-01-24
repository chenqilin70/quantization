package com.kylin.quantization.computors;

import com.google.common.collect.Lists;
import com.kylin.quantization.model.Index;
import com.kylin.quantization.model.IndexFundCorr;
import com.kylin.quantization.util.ResultUtil;
import com.kylin.quantization.util.RowKeyUtil;
import com.kylin.quantization.util.SqlConfigUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import scala.Function1;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.WrappedArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
//        commonDeal();

    }
    public static void gzbd(){
        JavaSparkContext sparkContext=new JavaSparkContext(sparkConf());
        SQLContext sqlContext=new SQLContext(sparkContext);
        registerHbaseTable("fund",sparkContext,sqlContext);
        Date start=new Date();
        DataFrame resultDF = sql("test", sqlContext);

        List<Tuple2<Integer, Tuple2<String, Integer>>> collect = resultDF.toJavaRDD().flatMapToPair(row -> {
            List<Tuple2<String, Integer>> result = new ArrayList<>();
            for (int i = 0; i < row.size(); i++) {
                WrappedArray arry = (WrappedArray) row.get(i);
                Iterator iterator = arry.iterator();
                while (!iterator.isEmpty()) {
                    Object next = iterator.next();
                    if (next == null) {
                        break;
                    } else {
                        String gzjz = ObjectUtils.toString(next);
                        String origin = gzjz;
                        if (gzjz.contains("×")) {
                            String[] split = gzjz.split("×");
                            if (split[0].contains("%")) {
                                gzjz = split[1];
                            } else {
                                gzjz = split[0];
                            }


                        }
                        if (gzjz.contains("*")) {
                            String[] split = gzjz.split("\\*");
                            if (split[0].contains("%")) {
                                gzjz = split[1];
                            } else {
                                gzjz = split[0];
                            }
                        }
                        result.add(new Tuple2<String, Integer>(
                                gzjz.replaceAll("收益率", "").replaceAll("\\d+%", "").trim()
                                , 1));
                    }
                }
            }
            return result;
        }).reduceByKey((i, j) -> i + j).mapToPair(t -> {
            return new Tuple2<Integer, Tuple2<String, Integer>>(t._2, t);
        }).sortByKey().collect();


        for(int k=0;k<collect.size();k++){
            Tuple2<Integer, Tuple2<String, Integer>> jz = collect.get(k);
            logger.info(jz._2._1+"==>"+jz._2._2);
        }
        Date end=new Date();
        logger.info("TestComputor is over ,and time is :"+((end.getTime()-start.getTime())/1000.00)+"s");
        /*registerHbaseTable("index",sparkContext,sqlContext);
        sql("index",sqlContext).show();*/


        sparkContext.stop();
    }








    private static Configuration getNetValConf()  {
        String tableName="netval";
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter closeFilter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("LJJZ"));
        Scan scan = new Scan()
                /*.setStartRow(Bytes.toBytes(RowKeyUtil.getNetValRowKey("161604","1949-10-01")))
                .setStopRow(Bytes.toBytes(RowKeyUtil.getNetValRowKey("161604", "2019-01-18")))*/
                .setFilter(closeFilter);

        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }







}
