package com.kylin.quantization.component;

import com.kylin.quantization.service.CatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportRunner   extends CatcherRunner {
    @Autowired
    private CatcherService service;
    @Override
    protected String getTask() {
        return "report";
    }

    @Override
    protected void doTask() {
        service.getReport();


    }
}
