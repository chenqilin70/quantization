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


public abstract  class CatcherRunner  implements ApplicationRunner {
    public static Logger logger= LoggerFactory.getLogger(CatcherRunner.class);


    protected abstract  String getTask();
    protected abstract  void doTask();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] sourceArgs = args.getSourceArgs();
        if(sourceArgs==null || sourceArgs.length==0){
            logger.warn(getTask()+"启动未传入参数");
        }else if(getTask().equals(sourceArgs[0])){
            logger.info(getTask()+"传入参数匹配成功："+sourceArgs[0]);
            doTask();
        }else{
            logger.info(getTask()+"传入参数匹配失败："+sourceArgs[0]);
        }

    }

    /*@Override
    public void run(ApplicationArguments args) throws Exception {
        //get fundlist
        List<Map<String,String>> fundList=service.getFundList();

        //baseData   fundlist-update,notinfundlist-noOpt
        FundBaseTask baseInfotask=new FundBaseTask(fundList,800,service);
        ForkJoinExecutor.exec(baseInfotask,20);


//        service.test();
//        SparkSubmit.main();


    }*/



}
