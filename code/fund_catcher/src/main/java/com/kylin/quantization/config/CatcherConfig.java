package com.kylin.quantization.config;

import com.kylin.quantization.util.MapUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class CatcherConfig {
    @Bean
    public Map<String,String> conf(){
        Map<String,String> conf=new HashMap<>();
        InputStream confin=CatcherConfig.class.getClassLoader().getResourceAsStream("conf.properties");
        Properties confPro=new Properties();
        try {
            confPro.load(confin);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(confin);
        }
        confPro.keySet().forEach(k->{
            conf.put(k.toString(), confPro.get(k).toString());
        });
        return conf;
    }
    @Bean
    public MapUtil<String,String> ssMapUtil(){
        return new MapUtil<String,String>();
    }
}
