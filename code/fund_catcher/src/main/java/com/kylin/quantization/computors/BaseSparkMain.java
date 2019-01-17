package com.kylin.quantization.computors;

import com.kylin.quantization.component.TestRunner;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import com.kylin.quantization.util.LoggerBuilder;
import com.kylin.quantization.util.ResultUtil;
import com.kylin.quantization.util.SqlConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

/**
 * ClassName: BaseSparkMain
 * Description:
 * Author: aierxuan
 * Date: 2019-01-03 10:22
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public abstract class BaseSparkMain {
    public static Logger logger = LoggerBuilder.build(BaseSparkMain.class);
    public static SparkConf sparkConf(){
        Map<String, String> sparkMap = CatcherConfig.proToMap("spark.properties");
        SparkConf conf = new SparkConf().setAppName(sparkMap.get("spark.appName"));
        conf.setMaster(sparkMap.get("spark.master"));
        //设置SparkHOME
        conf.setSparkHome(sparkMap.get("spark.sparkhome"));
        //设置运行资源参数
        conf.set("spark.executor.instances", sparkMap.get("spark.executor.instances"));
        conf.set("spark.executor.cores", sparkMap.get("spark.executor.cores"));
        conf.set("spark.executor.memory", sparkMap.get("spark.executor.memory"));
        conf.set("spark.driver.memory", sparkMap.get("spark.driver.memory"));
        conf.set("spark.driver.maxResultSize", sparkMap.get("spark.driver.maxResultSize"));
        return conf;
    }
    public static String convertScanToString(Scan scan) throws IOException {
        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        return Bytes.toString(java.util.Base64.getEncoder().encode(proto.toByteArray()));
    }



    public static <T> DataFrame getHbaseDataFrame(String tableName, Configuration hbaseConf, JavaSparkContext sparkContext, SQLContext sqlContext){
        final Class<T> clazz=getModelByTableName(tableName);
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = sparkContext.newAPIHadoopRDD(hbaseConf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        JavaRDD<T> indexRdd = hbaseRdd.map(t -> {

            Result result = t._2;
            String rowkey = ResultUtil.row(result);
            Field[] fields = clazz.getDeclaredFields();
            T model = clazz.newInstance();
            for (Field f : fields) {
                String fieldName = f.getName();
                String setMethodName="";
                Object setMethodParam="";
                Class paramClass=null;
                if ("rowkey".equals(fieldName)) {
                    setMethodName="setRowkey";
                    setMethodParam=rowkey;
                    paramClass=String.class;
                }else{
                    setMethodName="set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    paramClass=f.getType();
                    setMethodParam=transTypeFromString(ResultUtil.strVal(result, "baseinfo", fieldName),paramClass);
                    System.out.println("setMethodName:"+setMethodName);

                }
                Method setMethod = clazz.getMethod(setMethodName, paramClass);
                setMethod.invoke(model, setMethodParam);
            }
            return (T)model;
        });
        logger.info("collect size is :"+indexRdd.collect().size()+",tableName:"+tableName);
        DataFrame dataFrame = sqlContext.createDataFrame(indexRdd, clazz);

        return dataFrame;
    }

    private static Object transTypeFromString(String info,Class clazz) {
        Object obj=null;
        if(StringUtils.isNotBlank(info)){
            if(BigDecimal.class==clazz){
                obj=new BigDecimal(info);
            }else if(Integer.class==clazz){
                obj=Integer.parseInt(info);
            }else if(Double.class==clazz){
                obj=Double.parseDouble(info);
            }else if(Float.class==clazz){
                obj=Float.parseFloat(info);
            }else{
                obj=info;
            }
        }else{
            logger.warn("transTypeFromString `s param is invalid:"+info);
        }

        return obj;
    }

    public static <T>  DataFrame getHbaseDataFrame(String tableName,JavaSparkContext sparkContext, SQLContext sqlContext){
        return getHbaseDataFrame(tableName,getHbaseConf(tableName),sparkContext,sqlContext);
    }
    public static void registerHbaseTable(String tableName,Configuration hbaseConf,JavaSparkContext sparkContext, SQLContext sqlContext){
        DataFrame hbaseDataFrame = getHbaseDataFrame(tableName,hbaseConf,sparkContext,sqlContext);
        registerHbaseTable(tableName,hbaseDataFrame);
    }
    public static void registerHbaseTable(String tableName,JavaSparkContext sparkContext, SQLContext sqlContext){
        DataFrame hbaseDataFrame = getHbaseDataFrame(tableName,sparkContext,sqlContext);
        registerHbaseTable(tableName,hbaseDataFrame);
    }
    public static void registerHbaseTable(String tableName,DataFrame hbaseDataFrame ){
        hbaseDataFrame.registerTempTable(tableName);
    }
    private static Class getModelByTableName(String tableName){
        Class temp=null;
        try {
            temp= Class.forName("com.kylin.quantization.model."+tableName.substring(0,1).toUpperCase()+tableName.substring(1));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("getModelByTableName:"+temp.toString());
        return temp;
    }

    public static DataFrame sql(String sql,SQLContext sqlContext){
        logger.info("spark is exec sql :"+sql);
        DataFrame data = sqlContext.sql(SqlConfigUtil.getBizSql(sql));
        return data;

    }


    private static Configuration getHbaseConf(String tableName)  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
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
