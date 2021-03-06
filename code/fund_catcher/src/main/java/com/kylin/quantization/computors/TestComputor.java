package com.kylin.quantization.computors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.WrappedArray;

import java.io.IOException;
import java.util.ArrayList;
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
        commonDeal();

    }
    public static void gzbd(){
        JavaSparkContext sparkContext=new JavaSparkContext(sparkConf());
        SQLContext sqlContext=new SQLContext(sparkContext);
        registerHbaseTable("kzzlb",sparkContext,sqlContext);
        DataFrame df=sql("test",sqlContext);
        df.show(100);
        sparkContext.stop();
    }

    public static void commonDeal() {
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf());
        SQLContext sqlContext = new SQLContext(sparkContext);

        Date start = new Date();
        registerHbaseTable("index", getIndexConf(),sparkContext, sqlContext);
        registerHbaseTable("netval",getNetValConf(), sparkContext, sqlContext);
        registerHbaseTable("fund", sparkContext, sqlContext);
        DataFrame resultDF = sql("test", sqlContext);
        Row[] collect = resultDF.collect();
        for (int k = 0; k < collect.length; k++) {
            Row row = collect[k];
            for (int i = 0; i < row.size(); i++) {
                System.out.print(row.get(i) + "\t");
            }
            System.out.println("");
        }
        Date end = new Date();
        logger.info("TestComputor is over ,and time is :" + ((end.getTime() - start.getTime()) / 1000.00) + "s");
        /*registerHbaseTable("index",sparkContext,sqlContext);
        sql("index",sqlContext).show();*/


    }


    private static Configuration getIndexConf()  {
        String tableName="index";
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter closeFilter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("close"));
        Scan scan = new Scan()
//                .setStartRow(Bytes.toBytes(RowKeyUtil.getKlineRowkey("SH000300", "19491001")))
//                .setStopRow(Bytes.toBytes(RowKeyUtil.getKlineRowkey("SH000300", "20190125")))
                .setFilter(closeFilter);

        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
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
