package com.kylin.quantization.component;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.util.ESUtil;
import com.kylin.quantization.util.MapUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
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


        Map<String, String> source = new MapUtil<String, String>().create(
                "002070", "http://guba.eastmoney.com/list,002070,3,f_1.html" ,
               "601319","http://guba.eastmoney.com/news,601319,753909484.html"
        );

        source.keySet().stream().forEach(stockcode->{
            SearchRequestBuilder searchRequestBuilder = ESUtil.getEsClient().prepareSearch("stock_notice")
                    .setIndices("stock_notice").setTypes("stock_notice").setSize(0)
                    .setScroll(new TimeValue(60000)).addSort(SortBuilders.fieldSort("_doc"));

            searchRequestBuilder.setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery("htmlUrl.keyword", source.get(stockcode)))
                            .must(QueryBuilders.termQuery("stockcode.keyword", stockcode)));

            SearchResponse response = searchRequestBuilder.execute().actionGet();
            long totalCount = response.getHits().getTotalHits();
            logger.info(stockcode+" totalCount is "+totalCount);
            String scrollId=response.getScrollId();
            printHits(response.getHits());
            while(true){
                SearchScrollRequestBuilder searchScrollRequestBuilder = ESUtil.getEsClient().prepareSearchScroll(scrollId)
                        .setScroll(new TimeValue(60000));
                SearchResponse searchResponse = searchScrollRequestBuilder.execute().actionGet();
                scrollId=searchResponse.getScrollId();
                SearchHits hits = searchResponse.getHits();
                printHits(hits);
            }

        });






       /* Set<String> stocks=new HashSet<>();
        while(true){
            SearchScrollRequestBuilder searchScrollRequestBuilder = ESUtil.getEsClient().prepareSearchScroll(scrollId)
                    .setScroll(new TimeValue(60000));
            SearchResponse searchResponse = searchScrollRequestBuilder.execute().actionGet();
            scrollId=searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();
            if(hits.getHits().length==0){
                break;
            }
            for(SearchHit hit:hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Object stockcode = sourceAsMap.get("stockcode");
                stocks.add(stockcode.toString());
            }
        }
        logger.info("stocksize:"+stocks.size());
        stocks.forEach(s->{
            logger.info(s);
        });*/

    }

    public static void printHits(SearchHits hits){
        if(hits.getHits().length==0){
            logger.info("hits.getHits().length   :0");
            return;
        }
        for(SearchHit hit:hits){
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            sourceAsMap.remove("noticeContent");
            logger.info(JSON.toJSONString(sourceAsMap));
        }
    }



}
