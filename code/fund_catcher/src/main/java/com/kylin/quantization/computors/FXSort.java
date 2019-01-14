package com.kylin.quantization.computors;

import com.google.common.base.*;
import com.google.common.base.Optional;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import com.kylin.quantization.dao.impl.HBaseExecutors;
import com.kylin.quantization.util.RegexEnum;
import com.kylin.quantization.util.ResultUtil;
import com.kylin.quantization.util.RowKeyUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        JavaPairRDD<String, Result> fundRdd = hbaseRdd.filter(new Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>() {
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
        }).mapToPair(t -> new Tuple2<String, Result>(ResultUtil.strVal(t._2, "baseinfo", "fundcode"), t._2));

        JavaPairRDD<ImmutableBytesWritable, Result> netvalRdd = context.newAPIHadoopRDD(getNetValConf(), TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        JavaPairRDD<String, BigDecimal> codeNetvalRdd =  netvalRdd.filter(new Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>() {
            @Override
            public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                String ljjz=Bytes.toString(tuple2._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("LJJZ")));
                Matcher matcher=null;
                if(StringUtils.isNotBlank(ljjz)){
                    Pattern pattern=Pattern.compile(RegexEnum.NET_VAL_REG.val());
                    matcher=pattern.matcher(ljjz);
                }

                return StringUtils.isNotBlank(ljjz) &&  matcher.matches();
            }
        }).mapToPair(new PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, BigDecimal>() {
            @Override
            public Tuple2<String, BigDecimal> call(Tuple2<ImmutableBytesWritable, Result> t) throws Exception {
                String code=RowKeyUtil.getCodeFromRowkey(t._2.getRow());
                String ljjz=Bytes.toString(t._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("LJJZ")));
                BigDecimal netval=new BigDecimal(ljjz);
                return new Tuple2<String, BigDecimal>(code,netval);
            }
        });

//        JavaPairRDD<String, BigDecimal> codeValRdd = fundRdd.join(codeNetvalRdd).mapToPair(t -> new Tuple2<String, BigDecimal>(t._1, t._2._2));

/*

        JavaPairRDD<String, BigDecimal> sumRdd = codeValRdd.reduceByKey((v1, v2) -> v1.add(v2));
        JavaPairRDD<String, BigDecimal> countRdd=codeValRdd.mapToPair(t->new Tuple2<>(t._1,new BigDecimal("1"))).reduceByKey((i,j)->i.add(j));
        JavaPairRDD<String, BigDecimal> avgRdd=sumRdd.join(countRdd).mapToPair(tuple->new Tuple2<>(tuple._1,tuple._2._1.divide(tuple._2._2)));
        JavaPairRDD<String, BigDecimal> eRdd = codeValRdd.join(avgRdd)
                .mapToPair(tuple -> new Tuple2<String, BigDecimal>(tuple._1, tuple._2._1.subtract(tuple._2._2).pow(2)))
                .reduceByKey((v1,v2)->v1.add(v2));
        JavaPairRDD<String, BigDecimal> fxRdd = eRdd.leftOuterJoin(countRdd).mapToPair(tuple -> new Tuple2<String, BigDecimal>(tuple._1, tuple._2._1.divide(tuple._2._2.orNull())));
*/




        ;
//        List<Tuple2<String, BigDecimal>> collect = codeValRdd.collect();
//        logger.info("codeValRdd collect size:"+collect.size());
        /*JavaPairRDD<String, BigDecimal> filterRdd = codeNetvalRdd.filter(new Function<Tuple2<String, BigDecimal>, Boolean>() {
            private long count = 0;

            @Override
            public Boolean call(Tuple2<String, BigDecimal> v1) throws Exception {
                if (count < 100) {
                    count++;
                    return true;
                } else {
                    return false;
                }
            }
        });*/
        List<Tuple2<String, Result>> collect = fundRdd.collect();


        logger.info(" collect size:"+collect.size());
        collect.forEach(t->{
            logger.info("code:"+t._1+",fx:"+t._2);
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
        FilterList filterList=new FilterList(FilterList.Operator.MUST_PASS_ALL,filter1,filter2);
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
