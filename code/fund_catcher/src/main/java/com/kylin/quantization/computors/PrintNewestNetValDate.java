package com.kylin.quantization.computors;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import com.kylin.quantization.util.LoggerBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取fund的最新netval日期
 * ClassName: SparkWordCountWithJava7
 * Description:
 * Author: aierxuan
 * Date: 2018-12-28 14:05
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class PrintNewestNetValDate extends BaseSparkMain{
    public static Logger logger = LoggerBuilder.build(PrintNewestNetValDate.class);
    private static HBaseDao hBaseDao=new HBaseDaoImpl();
    static {
        hBaseDao.setHconfiguration(new CatcherConfig().hconfiguration());
    }


    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        Configuration hconf = getMaxNetValHconf();
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        List<Tuple2<String, Date>> collect = hbaseRdd.flatMapToPair(new PairFlatMapFunction<Tuple2<ImmutableBytesWritable, Result>, String, Date>() {

            @Override
            public Iterable<Tuple2<String, Date>> call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                List<Tuple2<String, Date>> result = new ArrayList<>();
                byte[] o = tuple._2().getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("FSRQ"));
                if (o != null) {
                    String date = Bytes.toString(o);
                    String code = Bytes.toString(tuple._1.get());
                    code = code.substring(code.indexOf("_"));
                    code = code.substring(0, code.lastIndexOf("_"));
                    code = code.replaceAll("_", "");
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
        logger.info("spark OK!");


        context.stop();
    }



    private static   Configuration getMaxNetValHconf()  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "netval";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
//        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("_"+code+"_"));
        Filter filter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("FSRQ"));
        Scan scan = new Scan().setFilter(filter);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }

}
