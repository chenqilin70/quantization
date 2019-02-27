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
    @Override
    protected String getTask() {
        return "stockNotice";
    }

    @Override
    protected void doTask() {
        String stock_list = HttpUtil.doGet(conf.get("stock_list"), CatcherConfig.proToMap("param/stock_list_param.properties"));
        System.out.println(stock_list);
        System.out.println("==============");
        String[] stock_infos = stock_list.substring(stock_list.indexOf("\"") + 1, stock_list.length() - 1).split("\",\"");
        List<String> codes = Arrays.asList(stock_infos).stream().map(s -> s.split(",")[1]).collect(Collectors.toList());
        codes.forEach(c-> System.out.println(c));
        System.out.println(codes.size());
    }
}
