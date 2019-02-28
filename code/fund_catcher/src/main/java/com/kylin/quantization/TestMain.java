package com.kylin.quantization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.component.StockNoticeRunner;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.util.*;
import org.apache.hadoop.hbase.client.Put;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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


public class TestMain {
    private static Map<String, String> conf = CatcherConfig.proToMap("conf.properties");
    private static MapUtil<String,String> ssMapUtil=new MapUtil<>();
    public static void main(String[] args) throws ParseException, IOException {
        dealDetail("http://guba.eastmoney.com/news,600069,806424930.html",new HashMap<>());
    }

    private static  void dealDetail(String href,Map<String,String> source) {
        String detailHtml = HttpUtil.doGet(href, null);
        Document doc = Jsoup.parse(detailHtml);

        Elements ps = doc.select("#zwconbody > div > p[class='publishdate']");
        String publishdate=ps.get(0).text().replaceAll("公告日期：","");

        Elements as = doc.select("#zwconbody > div > p  a[href^=\"http://pdf.dfcfw.com\"]");
        System.out.println("============="+as);
        String pdfhref = as.get(0).attr("href");
        CloseableHttpResponse closeableHttpResponse = HttpUtil.doGetFile(pdfhref);
        HttpEntity entity = closeableHttpResponse.getEntity();
        String text="";
        if(entity!=null){
            try {
                text= PDFUtil.getText(entity.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ssMapUtil.append(source,"noticeContent",text,"publishdate",publishdate);
        String sourceJson=JSON.toJSONString(source);

//        ESUtil.putData(sourceJson,"");
    }


}



































