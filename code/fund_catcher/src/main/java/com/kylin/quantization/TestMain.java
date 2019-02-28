package com.kylin.quantization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.util.*;
import org.apache.hadoop.hbase.client.Put;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


public class TestMain {
    private static Map<String, String> conf = CatcherConfig.proToMap("conf.properties");

    public static void main(String[] args) throws ParseException, IOException {
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
//        XContentBuilder.
        System.out.println();

    }
}



































