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
import org.apache.commons.io.FileUtils;
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
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
        BigDecimal chicang=new BigDecimal("1");
        BigDecimal zuigaojia=new BigDecimal("100");
        BigDecimal huichecanshu=new BigDecimal("0.08");
        BigDecimal duixianbili=new BigDecimal("0.22");
        BigDecimal huicheshouliancanshu=new BigDecimal("0.7");

        StringBuffer sb=new StringBuffer("[");

        while(true){
            System.out.println(chicang.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +new BigDecimal("100").subtract(zuigaojia).divide(new BigDecimal("100"),4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +zuigaojia.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huichecanshu.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +duixianbili.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huicheshouliancanshu.setScale(4,BigDecimal.ROUND_HALF_UP));

            sb.append("[");
            sb.append(new BigDecimal("100").subtract(zuigaojia).divide(new BigDecimal("100"),4,BigDecimal.ROUND_HALF_UP)+","
                            +chicang.setScale(4,BigDecimal.ROUND_HALF_UP)
                    );
            sb.append("],");

            zuigaojia=zuigaojia.multiply(new BigDecimal("1").subtract(huichecanshu));
            chicang=chicang.multiply(new BigDecimal("1").subtract(duixianbili));
            huichecanshu=huichecanshu.multiply(huicheshouliancanshu);
            if(chicang.doubleValue()<0.05){
                break;
            }


        }

        System.out.println(sb.substring(0,sb.lastIndexOf(","))+"]");






        /*for(int i=100;i>0;i--){
            BigDecimal xianjia=new BigDecimal(i);
            BigDecimal xiadiefu = zuigaojia.subtract(xianjia).divide(zuigaojia,4,BigDecimal.ROUND_HALF_UP);
            if(xiadiefu.compareTo(huichecanshu)>=0) {
                zuigaojia=xianjia;
                chicang=chicang.multiply(new BigDecimal("1").subtract(duixianbili));
                huichecanshu=huichecanshu.multiply(huicheshouliancanshu);
            }
            System.out.println(xianjia.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +chicang.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +zuigaojia.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huichecanshu.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +duixianbili.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huicheshouliancanshu.setScale(4,BigDecimal.ROUND_HALF_UP));
        }*/








    }




}



































