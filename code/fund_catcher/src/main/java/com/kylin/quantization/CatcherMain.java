package com.kylin.quantization;

import com.kylin.quantization.config.CatcherConfig;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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