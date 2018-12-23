package com.kylin.quantization.config;

import com.kylin.quantization.CatcherMain;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.HashSet;

/**
 * ClassName: BeforeRunner
 * Description:
 * Author: aierxuan
 * Date: 2018-12-23 11:57
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Configuration
@Order(1)
public class BeforeRunner implements CommandLineRunner{
    @Override
    public void run(String... args) throws Exception {
        System.out.println("----------------------------");

    }
}
