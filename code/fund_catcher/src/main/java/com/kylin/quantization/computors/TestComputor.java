package com.kylin.quantization.computors;

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
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
        String[] jars = System.getProperty("java.class.path").split(";");
        logger.info("============================");
        for(String jar:jars){
            logger.info(jar);
        }




        JavaSparkContext sparkContext=new JavaSparkContext(sparkConf());
        SQLContext sqlContext=new SQLContext(sparkContext);;
        String tableName="index";
        registerHbaseTable(tableName,sparkContext,sqlContext);
        sql(SqlConfigUtil.getBizSql("test"),sqlContext).show();
        sparkContext.stop();


    }





}
