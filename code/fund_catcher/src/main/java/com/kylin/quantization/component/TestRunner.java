package com.kylin.quantization.component;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.model.LoadDataModel;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.util.ESUtil;
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
        String indexName="stock_notice";
        for(int i=0;i<4;i++){
            ESUtil.putData(JSON.toJSONString(new LoadDataModel("/workspace/"+i,"netval"+i).setOverwrite(LoadDataModel.OVERWRITE_TABLE)),"stock_notice");
        }

    }



}
