package com.kylin.quantization.computors;

import com.kylin.quantization.model.Index;
import com.kylin.quantization.util.ResultUtil;
import com.kylin.quantization.util.RowKeyUtil;
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
    public static JavaSparkContext sparkContext=new JavaSparkContext(sparkConf());
    private static SQLContext sqlContext=null;

    public static SQLContext getSqlContext() {
        if(sqlContext==null){
            sqlContext=new SQLContext(sparkContext);
        }
        return sqlContext;
    }

    public static void main(String[] args) {
        String tableName="index";
        DataFrame dataFrame = getHbaseDataFrame(tableName);
        dataFrame.show();
        dataFrame.registerTempTable("index");
        getSqlContext().sql("select rowkey,close,timestamp from index where rowkey like 'SZ399006%' ").show();


    }
    public static <T> DataFrame  getHbaseDataFrame(String tableName){
        Configuration hbaseConf = getHbaseConf(tableName);
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = sparkContext.newAPIHadoopRDD(hbaseConf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        JavaRDD<Index> indexRdd = hbaseRdd.map(t -> {
            Result result = t._2;
            String rowkey = ResultUtil.row(result);
            Field[] fields = Index.class.getDeclaredFields();
            Index index = new Index();
            for (Field f : fields) {
                String fieldName = f.getName();
                if ("rowkey".equals(fieldName)) {
                    index.setRowkey(rowkey);
                    continue;
                }
                Method setMethod = Index.class.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), String.class);
                setMethod.invoke(index, ResultUtil.strVal(result, "baseinfo", fieldName));
            }
            return index;
        });
        DataFrame dataFrame = getSqlContext().createDataFrame(indexRdd, Index.class);
        return dataFrame;
    }



    private static Configuration getHbaseConf(String tableName)  {
        Configuration hconf= HBaseConfiguration.create();
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Scan scan = new Scan();
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }


}
