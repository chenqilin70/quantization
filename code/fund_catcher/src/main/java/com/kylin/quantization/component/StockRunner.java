package com.kylin.quantization.component;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
@Component
public class StockRunner  extends CatcherRunner{
    @Autowired
    private Map<String,String> conf;
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private CatcherService service;

    @Override
    protected String getTask() {
        return "stock";
    }

    @Override
    protected void doTask() {
        List<String> stockList = getStockList();
        ExecutorService pool= Executors.newFixedThreadPool(5);
        List<Future<Object>> futureList=new ArrayList<>();
        stockList.forEach(i->{
            Future<Object> submit = pool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    service.getStockVal(i);
                    return null;
                }
            });
            futureList.add(submit);
        });
        futureList.forEach(f->{
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        service.flushAndCompact("stock");
        pool.shutdown();

    }


    public List<String> getStockList(){
        String stock_list = HttpUtil.doGet(conf.get("stock_list"), CatcherConfig.proToMap("param/stock_list_param.properties"));
        String[] stock_infos = stock_list.substring(stock_list.indexOf("\"") + 1, stock_list.length() - 1).split("\",\"");
        List<String> codes = Arrays.asList(stock_infos).stream().map(s -> s.split(",")[1]).collect(Collectors.toList());
        return codes;
    }
}
