package com.kylin.quantization.component;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.thread.ForkJoinExecutor;
import com.kylin.quantization.thread.FundBaseTask;
import com.kylin.quantization.thread.NetValTask;
import com.kylin.quantization.util.MapUtil;
import org.apache.spark.deploy.SparkSubmit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

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

        //baseData   fundlist-update,notinfundlist-noOpt
        FundBaseTask baseInfotask=new FundBaseTask(fundList,800,service);
        ForkJoinExecutor.exec(baseInfotask,20);
        
        //netVal fundlist-append,notinfundlist-noOpt
        /*NetValTask netValTask=new NetValTask(fundList,800,service);
        ForkJoinExecutor.exec(netValTask,20);*/
//        service.test();
//        SparkSubmit.main();


    }
}
