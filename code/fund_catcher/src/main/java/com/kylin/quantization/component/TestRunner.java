package com.kylin.quantization.component;

import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.service.CatcherService;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private static final String HOST="192.168.109.205";
    private static final Integer PORT=9300;

    @Override
    protected String getTask() {
        return "test";
    }

    @Override
    protected void doTask() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        //创建客户端
        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddresses(
                            new TransportAddress(InetAddress.getByName(HOST),PORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("Elasticsearch connect info:" + client.toString());


        IndexResponse response = null;
        try {
            response = client.prepareIndex("testindex", "testindex", "1").setSource(
                    XContentFactory.jsonBuilder()
                            .startObject().field("userName", "张三")
                            .field("sendDate", new Date())
                            .field("msg", "你好李四")
                            .endObject()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("索引名称:" + response.getIndex() + "\n类型:" + response.getType()
                + "\n文档ID:" + response.getId() + "\n当前实例状态:" + response.status());




        //关闭客户端
        client.close();
    }



}
