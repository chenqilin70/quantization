package com.kylin.quantization.computors;

import com.google.common.base.*;
import com.google.common.base.Optional;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import com.kylin.quantization.util.RowKeyUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
public class FXSort extends BaseSparkMain{
    public static Logger logger = Logger.getLogger(FXSort.class);
    private static HBaseDao hBaseDao=new HBaseDaoImpl();
    static {
        hBaseDao.setHconfiguration(new CatcherConfig().hconfiguration());
    }


    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        Configuration hconf = getFundListConf();
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        /*JavaPairRDD<String, BigDecimal> codeValRdd = hbaseRdd.filter(new Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>() {
            @Override
            public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                boolean flg = false;
                byte[] value = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("fxrq"));
                SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
                if (value != null && value.length != 0) {
                    String fxrq = Bytes.toString(value);
                    Date fxrqDate = sf.parse(fxrq);
                    Calendar ago = Calendar.getInstance();
                    ago.add(Calendar.YEAR, -1);
                    if (ago.getTime().getTime() >= fxrqDate.getTime()) {
                        flg = true;
                    }

                }
                return flg;
            }
        }).flatMapToPair(new PairFlatMapFunction<Tuple2<ImmutableBytesWritable, Result>, String, BigDecimal>() {
            @Override
            public Iterable<Tuple2<String, BigDecimal>> call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
                List<Tuple2<String, BigDecimal>> result = new LinkedList<>();
                String code = RowKeyUtil.getCodeFromRowkey(tuple._1);
                Filter filter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("LJJZ"));
                Filter filter3 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("FSRQ"));
                FilterList qualifierFilter = new FilterList(FilterList.Operator.MUST_PASS_ONE, filter2, filter3);
                Scan scan = new Scan().setFilter(qualifierFilter)
                        .setStartRow(RowKeyUtil.getNetValRowKeyArray(code,"1970-01-01"))
                        .setStopRow(RowKeyUtil.getNetValRowKeyArray(code,sf.format(new Date())));
                hBaseDao.table("netval", table -> {
                    ResultScanner scanner = table.getScanner(scan);
                    scanner.forEach(r -> {
                        byte[] value = r.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("LJJZ"));
                        if (value != null && value.length != 0) {
                            Tuple2<String, BigDecimal> t = new Tuple2<>(code, new BigDecimal(Bytes.toString(value)));
                            result.add(t);
                        }
                    });
                    return null;
                });
                return result;
            }
        });*/


        /*List<Tuple2<ImmutableBytesWritable, Result>> collect = hbaseRdd.filter(new Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>() {
            @Override
            public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                *//*boolean flg = false;
                byte[] value = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("fxrq"));
                SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
                if (value != null && value.length != 0) {
                    String fxrq = Bytes.toString(value);
                    Date fxrqDate = sf.parse(fxrq);
                    Calendar ago = Calendar.getInstance();
                    ago.add(Calendar.YEAR, -1);
                    if (ago.getTime().getTime() >= fxrqDate.getTime()) {
                        flg = true;
                    }

                }*//*
                return true;
            }
        }).collect();*/
        List<Tuple2<ImmutableBytesWritable, Result>> collect = hbaseRdd.collect();
        logger.info("size:"+collect.size());
        collect.forEach(t->{
            logger.info("_1:"+Bytes.toString(t._1.get())+",_2:"+t._2);
        });


        /*
        JavaPairRDD<String, BigDecimal> sumRdd = codeValRdd.reduceByKey((v1, v2) -> v1.add(v2));
        JavaPairRDD<String, BigDecimal> countRdd=codeValRdd.mapToPair(t->new Tuple2<>(t._1,new BigDecimal("1"))).reduceByKey((i,j)->i.add(j));
        JavaPairRDD<String, BigDecimal> avgRdd=sumRdd.leftOuterJoin(countRdd).mapToPair(tuple->new Tuple2<>(tuple._1,tuple._2._1.divide(tuple._2._2.get())));
        JavaPairRDD<String, BigDecimal> eRdd = codeValRdd.leftOuterJoin(avgRdd).mapToPair(tuple -> new Tuple2<String, BigDecimal>(tuple._1, tuple._2._1.subtract(tuple._2._2.get()).pow(2)));
        JavaPairRDD<String, BigDecimal> fxRdd = eRdd.leftOuterJoin(countRdd).mapToPair(tuple -> new Tuple2<String, BigDecimal>(tuple._1, tuple._2._1.divide(tuple._2._2.orNull())));
        List<Tuple2<String, BigDecimal>> collect = fxRdd.collect();
        logger.info("collect size:"+collect.size());
        collect.forEach(t->{
            logger.info("code:"+t._1+",fx:"+t._2);
        });*/
        logger.info("spark OK!");
        context.stop();
    }



    private static   Configuration getFundListConf()  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "fund";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
//        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("_"+code+"_"));
//        Filter filter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("FSRQ"));
        Filter filter1 =new SingleColumnValueFilter(Bytes.toBytes("baseinfo"),Bytes.toBytes("jjlx"),CompareFilter.CompareOp.EQUAL,new RegexStringComparator("股票型"));
//        Filter filter2 =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("fxrq"));
//        Filter filter3=new PageFilter(1000);
//        FilterList filterList=new FilterList(FilterList.Operator.MUST_PASS_ALL,filter1,filter2);
        Scan scan = new Scan().setFilter(filter1);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }

}
