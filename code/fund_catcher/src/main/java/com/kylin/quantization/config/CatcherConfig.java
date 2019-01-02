package com.kylin.quantization.config;

import com.kylin.quantization.CatcherMain;
import com.kylin.quantization.component.CatcherRunner;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import com.kylin.quantization.util.MapUtil;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;


import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Configuration
@PropertySource(value = { "classpath:/spark.properties"})
public class CatcherConfig  {
    public static Logger logger= LoggerFactory.getLogger(CatcherConfig.class);
    @Value("${env}")
    private String env;
    @Value("${spark.driver.maxResultSize}")
    private String driverMaxResultSize;

    @Value("${spark.master}")
    private String master;
    @Value("${spark.jar}")
    private String jar;
    @Value("${spark.sparkhome}")
    private String sparkhome;
    @Value("${spark.executor.instances}")
    private String executorInstances;
    @Value("${spark.executor.cores}")
    private String executorCores;
    @Value("${spark.executor.memory}")
    private String executorMemory;
    @Value("${spark.driver.memory}")
    private String driverMemory;
    @Value("${spark.appName}")
    private String appName;

    @Bean
    public Map<String,String> conf(){
        Map<String,String> conf=proToMap("conf.properties");
        PropertyConfigurator.configure(CatcherMain.class.getClassLoader()
                .getResourceAsStream("log4j_"+env+".properties"));
        logger.info("使用日志配置文件:log4j_"+env+".properties");
        return conf;
    }


    @Bean
    public Map<String,String> columnCode(){
        return proToMap("column.properties");
    }

    public static Map<String,String> proToMap(String fileName){
        Map<String,String> result=new HashMap<>();
        InputStream in=CatcherConfig.class.getClassLoader().getResourceAsStream(fileName);
        Properties pro=new Properties();
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(in);
        }
        pro.keySet().forEach(k->{
            result.put(k.toString(), pro.get(k).toString());
        });
        return result;
    }

    @Bean
    public MapUtil<String,String> ssMapUtil(){
        return new MapUtil<String,String>();
    }

    @Bean
    public org.apache.hadoop.conf.Configuration hconfiguration(){
        return HBaseConfiguration.create();
    }

    @Bean
    public SparkConf sparkConf(){
        SparkConf conf = new SparkConf().setAppName(appName);
        /*yarn-client模式*/
        conf.setMaster(master);
        //设置程序包
        conf.setJars(new String[]{jar});
        //设置SparkHOME
        conf.setSparkHome(sparkhome);
        //设置运行资源参数
        conf.set("spark.executor.instances", executorInstances);
        conf.set("spark.executor.cores", executorCores);
        conf.set("spark.executor.memory", executorMemory);
        conf.set("spark.driver.memory", driverMemory);
        conf.set("spark.driver.maxResultSize", driverMaxResultSize);
        return conf;
    }


}
