package com.kylin.quantization.component;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StockNoticeRunner  extends CatcherRunner{
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private Map<String,String> conf;
    @Autowired
    private StockRunner stockRunner;
    @Override
    protected String getTask() {
        return "stockNotice";
    }

    @Override
    protected void doTask() {
        stockRunner.getStockList();
    }
}
