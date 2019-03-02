package com.kylin.quantization.component;

import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.util.ESUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * ClassName: TestRunner
 * Description:
 * Author: aierxuan
 * Date: 2018-12-23 9:48
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Component
public class TestRunner extends CatcherRunner {
    public static Logger logger = Logger.getLogger(TestRunner.class);
    @Autowired
    private CatcherService service ;
    @Autowired
    private HBaseDao hBaseDao;

    @Override
    protected String getTask() {
        return "test";
    }

    @Override
    protected void doTask() {
        SearchResponse response = ESUtil.getEsClient().prepareSearch("stock_notice")
                .fields("stockcode").setTypes("stock_notice")
                .setSize(10).setScroll(new TimeValue(2000)).execute()
                .actionGet();
        String scrollId=response.getScrollId();
        Set<String> stocks=new HashSet<>();
        while(true){
            SearchScrollRequestBuilder searchScrollRequestBuilder = ESUtil.getEsClient().prepareSearchScroll(scrollId);
            SearchResponse searchResponse = searchScrollRequestBuilder.get();
            scrollId=searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();
            if(hits.getHits().length==0){
                break;
            }
            Iterator<SearchHit> iterator = hits.iterator();
            while(iterator.hasNext()){
                SearchHit next = iterator.next();
                if(next==null){
                    break;
                }
                logger.info("getSourceAsMap:"+next.getSourceAsMap());
                logger.info("getSourceAsString:"+next.getSourceAsString());
                Object stockcode = next.getSourceAsString();
                stocks.add(stockcode.toString());
            }
        }
        logger.info("stocksize:"+stocks.size());
        stocks.forEach(s->{
            logger.info(s);
        });

    }



}
