package com.kylin.quantization.component;

import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.thread.ForkJoinExecutor;
import com.kylin.quantization.thread.FundBaseTask;
import com.kylin.quantization.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class FundBaseRunner extends CatcherRunner {
    public static Logger logger= LoggerFactory.getLogger(FundBaseRunner.class);
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private CatcherService service;


    @Override
    protected String getTask() {
        return "fund";
    }

    @Override
    protected void doTask() {
        //get fundlist
        List<Map<String,String>> fundList=service.getFundList();
        //baseData   fundlist-update,notinfundlist-noOpt
        FundBaseTask baseInfotask=new FundBaseTask(fundList,800,service);
        ForkJoinExecutor.exec(baseInfotask,20);
        service.flushAndCompact("fund");
    }

}
