package com.kylin.quantization.component;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.config.CatcherConfig;
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
    private StockRunner stockRunner;
    @Override
    protected String getTask() {
        return "stockNotice";
    }

    @Override
    protected void doTask() {
        for(String stockcode:stockRunner.getStockList()){
            String html = HttpUtil.doGet(StringReplaceUtil.replace(conf.get("stock_notice_list"),ssMapUtil.create("pageno","1","stockcode",stockcode)), null);
            Document parseHtml = Jsoup.parse(html);
//        System.out.println(html);
            Elements divs = parseHtml.getElementsByClass("articleh normal_post");
//        System.out.println(divs.size());
            for(Element e:divs){
                Elements spans = e.getElementsByTag("span");
                Element a = spans.get(2).getElementsByTag("a").get(0);
                String href = conf.get("stock_notice_detail")+a.attr("href");
                String title=a.attr("title");
                dealDetail(href,ssMapUtil.create("stockcode",stockcode,"title",title));
            }
        }

    }




    private  void dealDetail(String href,Map<String,String> source) {
        String detailHtml = HttpUtil.doGet(href, null);
        Document doc = Jsoup.parse(detailHtml);

        Elements ps = doc.select("#zwconbody > div > p[class='publishdate']");
        String publishdate=ps.get(0).text().replaceAll("公告日期：","");

        Elements as = doc.select("#zwconbody > div > p > a[href^=\"http://pdf.dfcfw.com\"]");
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

        ESUtil.putData(sourceJson,"stock_notice");
    }


}
