package com.kylin.quantization.component;

import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.thread.ForkJoinExecutor;
import com.kylin.quantization.thread.NetValTask;
import com.kylin.quantization.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class NetValRunner extends CatcherRunner{
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private CatcherService service;
    @Override
    protected String getTask() {
        return "netval";
    }

    @Override
    protected void doTask() {
        //get fundlist
        List<Map<String,String>> fundList=service.getFundList();
        //netVal fundlist-append,notinfundlist-noOpt
        NetValTask netValTask=new NetValTask(fundList,800,service);
        ForkJoinExecutor.exec(netValTask,20);
        service.flushAndCompact("netval");
    }
}
