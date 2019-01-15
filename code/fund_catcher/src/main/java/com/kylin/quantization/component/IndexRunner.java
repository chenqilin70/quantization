package com.kylin.quantization.component;

import com.kylin.quantization.service.CatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexRunner  extends CatcherRunner {
    @Autowired
    private CatcherService service;

    @Override
    protected String getTask() {
        return "index";
    }

    @Override
    protected void doTask() {
        service.getIndexVal("SH000001");
    }
}
