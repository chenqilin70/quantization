package com.kylin.quantization.component;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.thread.ForkJoinExecutor;
import com.kylin.quantization.thread.StockNoticeTask;
import com.kylin.quantization.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private CatcherService service;
    @Autowired
    private StockRunner stockRunner;
    public static final String INDEX="stock_notice";
    @Override
    protected String getTask() {
        return "stockNotice";
    }

    @Override
    protected void doTask() {
        if(ESUtil.isExists(INDEX)){
            ESUtil.deleteIndex(INDEX);
        }
        ESUtil.createIndex(INDEX,5,0);
        StockNoticeTask stockNoticeTask = new StockNoticeTask(stockRunner.getStockList(), 600, service);
        ForkJoinExecutor.exec(stockNoticeTask,20);
    }







}
