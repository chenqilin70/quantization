package com.kylin.quantization;

import com.kylin.quantization.util.HttpUtil;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: CatcherMain
 * Description:
 * Author: aierxuan
 * Date: 2018-12-21 11:23
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */

@SpringBootApplication
public class CatcherMain   {

    public static void main(String[] args) {
        SpringApplication app=new SpringApplication(CatcherMain.class);
        app.setBannerMode(Banner.Mode.OFF); app.run(args);



    }



}