package com.kylin.quantization.component;

import com.kylin.quantization.service.CatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class IndexRunner  extends CatcherRunner {
    @Autowired
    private CatcherService service;
    @Autowired
    private Map<String,String> conf;

    @Override
    protected String getTask() {
        return "index";
    }

    @Override
    protected void doTask() {
        String indexs = conf.get("indexs");
        String[] split = indexs.split(",");
        List<String> indexList = Arrays.asList(split).stream().map(i -> i.substring(0, i.indexOf("/"))).collect(Collectors.toList());
        ExecutorService pool= Executors.newFixedThreadPool(5);
        List<Future<Object>> futureList=new ArrayList<>();
        indexList.forEach(i->{
            Future<Object> submit = pool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    service.getIndexVal(i);
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
        service.flushAndCompact("index");
        pool.shutdown();

    }
}
