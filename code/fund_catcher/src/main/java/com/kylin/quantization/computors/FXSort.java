package com.kylin.quantization.computors;

import com.google.common.base.*;
import com.google.common.base.Optional;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import com.kylin.quantization.functions.FxFunctions;
import com.kylin.quantization.util.ResultUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 方差计算
 * ClassName: SparkWordCountWithJava7
 * Description:
 * Author: aierxuan
 * Date: 2018-12-28 14:05
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class FXSort extends BaseSparkMain{
    public static final String START_DATE="2014年01月01日";
    public static final String END_DATE="2014年12月31日";
    public static final Integer LEAST_YEARS=5;

    public static Logger logger = Logger.getLogger(FXSort.class);
    public static HBaseDao hBaseDao=new HBaseDaoImpl();
    static {
        hBaseDao.setHconfiguration(new CatcherConfig().hconfiguration());
    }


    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext(sparkConf());
        Configuration hconf = getFundListConf();

        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = context.newAPIHadoopRDD(hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        //获取fund列表
        JavaPairRDD<String, Result> fundRdd = hbaseRdd.filter(new FxFunctions.FundFilterFunction())
                .mapToPair(t -> new Tuple2<String, Result>(ResultUtil.strVal(t._2, "baseinfo", "fundcode"), t._2));

        JavaPairRDD<ImmutableBytesWritable, Result> netvalRdd = context.newAPIHadoopRDD(getNetValConf(), TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        //获取netval列表
        JavaPairRDD<String, BigDecimal> codeNetvalRdd =  netvalRdd.filter(new FxFunctions.NetvalFilterFunction())
                .mapToPair(new FxFunctions.CodeNetvalRddFunction());
        //筛选出在fund列表中存在的netval
        JavaPairRDD<String, BigDecimal> codeValRdd = fundRdd.join(codeNetvalRdd).mapToPair(t -> new Tuple2<String, BigDecimal>(t._1, t._2._2));

        //计算每个fund的netval和
        JavaPairRDD<String, BigDecimal> sumRdd = codeValRdd.reduceByKey((v1, v2) -> v1.add(v2));
        //计算每个fund的netval总数
        JavaPairRDD<String, BigDecimal> countRdd=codeValRdd.mapToPair(t->new Tuple2<>(t._1,new BigDecimal("1"))).reduceByKey((i,j)->i.add(j));
        //计算每个fund的netval平均数
        JavaPairRDD<String, BigDecimal> avgRdd=sumRdd.join(countRdd).mapToPair(tuple->new Tuple2<>(tuple._1,tuple._2._1.divide(tuple._2._2,4,BigDecimal.ROUND_HALF_UP)));
        //计算每个fund的netval方差的分子
        JavaPairRDD<String, BigDecimal> eRdd = codeValRdd.join(avgRdd)
                .mapToPair(tuple -> new Tuple2<String, BigDecimal>(tuple._1, tuple._2._1.subtract(tuple._2._2).pow(2)))
                .reduceByKey((v1,v2)->v1.add(v2));
        //计算每个fund的netval的方差
        JavaPairRDD<BigDecimal, Tuple2<String, BigDecimal>> fxRdd = eRdd.leftOuterJoin(countRdd).mapToPair(new FxFunctions.FxSortFunction());
        JavaPairRDD<BigDecimal, Tuple2<String, BigDecimal>> sortRdd = fxRdd.sortByKey();


        List<Tuple2<BigDecimal, Tuple2<String, BigDecimal>>> collect = sortRdd.collect();
        logger.info("collect size:"+collect.size());
        collect.forEach(t->{
            logger.info("code:"+t._2._1+",fx:"+t._2._2);
        });
        logger.info("spark OK!");
        context.stop();
    }



    private static   Configuration getFundListConf()  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "fund";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter filter1 =new SingleColumnValueFilter(Bytes.toBytes("baseinfo"),Bytes.toBytes("jjlx"),CompareFilter.CompareOp.EQUAL,new RegexStringComparator("股票型"));
        Filter filter2 =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("fxrq"));
        Filter filter3 =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("fundcode"));
        FilterList qulifierFilterList=new FilterList(FilterList.Operator.MUST_PASS_ONE,filter2,filter3);
        FilterList filterList=new FilterList(FilterList.Operator.MUST_PASS_ALL,filter1,qulifierFilterList);
        Scan scan = new Scan().setFilter(filterList);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }
    private static   Configuration getNetValConf()  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "netval";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter filter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("LJJZ"));
        Filter filter3 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("FSRQ"));
        FilterList qualifierFilter = new FilterList(FilterList.Operator.MUST_PASS_ONE, filter2, filter3);
        Scan scan = new Scan().setFilter(qualifierFilter);
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }

}
