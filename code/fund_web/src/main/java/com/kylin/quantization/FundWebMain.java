package com.kylin.quantization;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.kylin.quantization.mapper")
public class FundWebMain {
    public static void main(String[] args) {
        SpringApplication.run(FundWebMain.class,args);
    }
}
