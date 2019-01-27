package com.kylin.quantization.computors;

import com.kylin.quantization.thread.CorrTask;
import com.kylin.quantization.thread.ForkJoinExecutor;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * ClassName: CorrComputor
 * Description:
 * Author: aierxuan
 * Date: 2019-01-24 17:03
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class CorrComputor  extends BaseSparkMain{
    public static void main(String[] args) {
        JavaSparkContext sparkContext=new JavaSparkContext(sparkConf());
        SQLContext sqlContext=new SQLContext(sparkContext);

        Date start=new Date();
        registerHbaseTable("index",getIndexConf(),sparkContext,sqlContext);
        registerHbaseTable("netval",getNetValConf(),sparkContext,sqlContext);
        registerHbaseTable("fund",sparkContext,sqlContext);
        DataFrame resultDF = sql("corr", sqlContext);
        Row[] collect = resultDF.collect();
        CorrTask corrtask=new CorrTask(Arrays.asList(collect),800);
        ForkJoinExecutor.exec(corrtask, 20);


        Date end=new Date();
        logger.info("TestComputor is over ,and time is :"+((end.getTime()-start.getTime())/1000.00)+"s");
        /*registerHbaseTable("index",sparkContext,sqlContext);
        sql("index",sqlContext).show();*/


        sparkContext.stop();
    }





    private static Configuration getIndexConf()  {
        String tableName="index";
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter closeFilter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("close"));
        Scan scan = new Scan()
//                .setStartRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000300", "19491001")))
//                .setStopRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000300", "20190125")))
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
