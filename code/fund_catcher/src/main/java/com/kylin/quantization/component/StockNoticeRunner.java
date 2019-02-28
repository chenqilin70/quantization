package com.kylin.quantization.component;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.MapUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
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
    private StockRunner stockRunner;
    @Override
    protected String getTask() {
        return "stockNotice";
    }

    @Override
    protected void doTask() {
        stockRunner.getStockList();
    }




    public void createStudentIndex(TransportClient client, String indexName) {
        CreateIndexRequestBuilder cib = client.admin().indices().prepareCreate(indexName);
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder()
                    .startObject()//表示开始设置值
                    .startObject("properties")//设置只定义字段，不传参
                    .startObject("no") //定义字段名
                    .field("type", "text") //设置数据类型
                    .endObject()
                    .startObject("name")
                    .field("type", "text")
                    .endObject()
                    .startObject("addreess")
                    .field("type", "text")
                    .endObject()
                    .startObject("age")
                    .field("type", "integer")
                    .endObject()
                    .startObject("phone")
                    .field("type", "text")
                    .endObject()
                    .startObject("score")
                    .field("type", "integer")
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cib.addMapping("student", mapping);
        cib.execute().actionGet();

    }

}
