package com.kylin.quantization;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan("com.kylin.quantization.mapper")
//@ComponentScan(basePackages = {"com.kylin.quantization"})
public class FundWebMain {
    public static void main(String[] args) {
        SpringApplication.run(FundWebMain.class,args);
    }
}
