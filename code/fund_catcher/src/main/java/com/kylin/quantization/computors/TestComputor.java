package com.kylin.quantization.computors;

import com.google.common.collect.Lists;
import com.kylin.quantization.model.Index;
import com.kylin.quantization.util.ResultUtil;
import com.kylin.quantization.util.RowKeyUtil;
import com.kylin.quantization.util.SqlConfigUtil;
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
import scala.Tuple2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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

        JavaSparkContext sparkContext=new JavaSparkContext(sparkConf());
        SQLContext sqlContext=new SQLContext(sparkContext);

        Date start=new Date();
        registerHbaseTable("index",sparkContext,sqlContext);
        registerHbaseTable("netval",sparkContext,sqlContext);
        sql("test",sqlContext).show();
        Date end=new Date();
        logger.info("TestComputor is over ,and time is :"+((end.getTime()-start.getTime())/1000.00)+"s");
        /*registerHbaseTable("index",sparkContext,sqlContext);
        sql("index",sqlContext).show();*/


        sparkContext.stop();
    }




    private static Configuration getIndexConf(String tableName)  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter closeFilter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("close"));
        Filter macdFilter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("macd"));
        Scan scan = new Scan()
                .setStartRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000300", "20190101")))
                .setStopRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000300", "20190116")))
                .setFilter(new FilterList(FilterList.Operator.MUST_PASS_ONE,closeFilter,macdFilter));

        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }






}
