package com.kylin.quantization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.component.StockNoticeRunner;
import com.kylin.quantization.component.StockRunner;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.thread.BaseRecursiveTask;
import com.kylin.quantization.thread.ForkJoinExecutor;
import com.kylin.quantization.thread.StockNoticeTask;
import com.kylin.quantization.util.*;
import org.apache.hadoop.hbase.client.Put;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMain {
    private static Map<String, String> conf = CatcherConfig.proToMap("conf.properties");
    private static MapUtil<String,String> ssMapUtil=new MapUtil<>();
    public static void main(String[] args) throws ParseException, IOException {
        String stock_list = HttpUtil.doGet(conf.get("stock_list"), CatcherConfig.proToMap("param/stock_list_param.properties"));
        String[] stock_infos = stock_list.substring(stock_list.indexOf("\"") + 1, stock_list.length() - 1).split("\",\"");
        List<String> codes = Arrays.asList(stock_infos).stream().map(s -> s.split(",")[1]).collect(Collectors.toList());
        TestTask task = new TestTask(codes, 200);
        ForkJoinExecutor.exec(task,20);

    }

    public static class TestTask extends BaseRecursiveTask<String,Object> {

        public TestTask(List<String> datas, int THRESHOLD_NUM) {
            super(datas, THRESHOLD_NUM);
        }

        @Override
        public Object run(List<String> perIncrementList) {
            perIncrementList.forEach(p->{
                System.out.println(p);
            });
            return null;
        }

        @Override
        protected Object reduce(Object leftResult, Object rightResult) {
            return null;
        }

        @Override
        public BaseRecursiveTask getBaseRecursiveTask(List<String> dataList, int THRESHOLD_NUM) {
            return new TestTask(dataList,THRESHOLD_NUM);
        }
    }



}



































