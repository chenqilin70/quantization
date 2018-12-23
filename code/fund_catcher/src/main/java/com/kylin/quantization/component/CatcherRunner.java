package com.kylin.quantization.component;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//@Component
public class CatcherRunner  implements ApplicationRunner {
    public static Logger logger= LoggerFactory.getLogger(CatcherRunner.class);
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private CatcherService service;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //get fundlist
        List<Map<String,String>> fundList=service.getFundList();
        //forEach list
        fundList.forEach(fund->{
            //get base info and save to hbase
            List<Map<String,String>> fundbase=service.getFucndBase(fund);
            System.out.println("sdf");
            //get net val and save to hbase
        });

    }
}
