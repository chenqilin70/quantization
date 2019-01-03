package com.kylin.quantization.computors;

import com.kylin.quantization.config.CatcherConfig;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;

import java.io.IOException;
import java.util.Map;

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
    public static SparkConf sparkConf(){
        Map<String, String> sparkMap = CatcherConfig.proToMap("spark.properties");
        SparkConf conf = new SparkConf().setAppName(sparkMap.get("spark.appName"));
        /*yarn-client模式*/
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

}
