package com.kylin.quantization.config;

import com.kylin.quantization.CatcherMain;
import com.kylin.quantization.component.CatcherRunner;
import com.kylin.quantization.dao.impl.HBaseDaoImpl;
import com.kylin.quantization.util.MapUtil;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Configuration
public class CatcherConfig  {
    public static Logger logger= LoggerFactory.getLogger(CatcherConfig.class);
    @Value("${env}")
    private String env;
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
    public HBaseDaoImpl hBaseDaoImpl(){
        return new HBaseDaoImpl().init();
    }


}
