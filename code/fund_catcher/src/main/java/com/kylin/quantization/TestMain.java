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


public class TestMain {
    private static Map<String, String> conf = CatcherConfig.proToMap("conf.properties");
    private static MapUtil<String,String> ssMapUtil=new MapUtil<>();
    public static void main(String[] args) throws ParseException, IOException {
        String hrefs="http://guba.eastmoney.com/news,600127,789847771.html;http://guba.eastmoney.com/news,603037,789854823.html;http://guba.eastmoney.com/news,600928,806275529.html;http://guba.eastmoney.com/news,002567,805284141.html;http://guba.eastmoney.com/news,002056,789880848.html;http://guba.eastmoney.com/news,002128,789935018.html;http://guba.eastmoney.com/news,002060,806426048.html;http://guba.eastmoney.com/news,of002778,781507876.html;http://guba.eastmoney.com/news,002274,801987990.html";
//        String hrefs="http://guba.eastmoney.com/news,002056,789880848.html";
        for(String href:hrefs.split(";")){
            dealDetail(href,new HashMap<>());
        }

        /*String s="发表于 2018-10-30 16:51:46 股吧网页版";
        Pattern pattern=Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(s);
        System.out.println(matcher.find());
        System.out.println(matcher.group());
        System.out.println(matcher.find());*/


    }

    private static void dealDetail(String href,Map<String,String> source) {
        String detailHtml = HttpUtil.doGet(href, null);
        Document doc = Jsoup.parse(detailHtml);

        Elements posttimeEles = doc.select("#zwconbody > div > p[class='publishdate']");
        String publishdate="";
        if(posttimeEles.size()==0){
//            System.out.println("ps为空,href:"+href+",doc:\n"+doc.select("#zwconbody > div > p").toString());zwfbtime
            posttimeEles=doc.select(".post_time");
            if(posttimeEles.size()==0 || posttimeEles.text().length()<9){
                posttimeEles=doc.select(".zwfbtime");
                String posttime=posttimeEles.get(0).text();

                Pattern pattern=Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
                Matcher matcher = pattern.matcher(posttime);
                matcher.find();
                publishdate=matcher.group();
            }else{
                publishdate=posttimeEles.get(0).text().substring(0,10);
            }

        }else{
            publishdate=posttimeEles.get(0).text().replaceAll("公告日期：","");
        }


        Elements as = doc.select("#zwconbody > div > p a[href^=\"http://pdf.dfcfw.com\"]");
        if(as.size()==0){
            as=doc.select(".zwtitlepdf a");
//            System.out.println("as为空,href:"+href+",doc:\n"+doc.select("#zwconbody > div > p").toString());
        }
        String filehref = as.get(0).attr("href");
        CloseableHttpResponse closeableHttpResponse = HttpUtil.doGetFile(filehref);
        HttpEntity entity = closeableHttpResponse.getEntity();
        String text="";
        if(entity!=null){
            try {
                if(filehref.endsWith("pdf")){
                        text= PDFUtil.getText(entity.getContent());
                }else if(filehref.endsWith("txt")){
                    text= EntityUtils.toString(entity,"gbk");
                }else if(filehref.endsWith("doc")){
                    InputStream inputStream = entity.getContent();

                    try{

                        HWPFDocument hwpfDocument = new HWPFDocument(inputStream);
                        text=hwpfDocument.getText().toString();
                    }catch (IllegalArgumentException e){
                        CloseableHttpResponse tempResponse = HttpUtil.doGetFile(filehref);
                        XWPFDocument xdoc = new XWPFDocument(tempResponse.getEntity().getContent());
                        POIXMLTextExtractor extractor = new XWPFWordExtractor(xdoc);
                        text = extractor.getText();
                        tempResponse.close();
                    }

                }else {
                    throw new RuntimeException("文件格式无法解析：href:"+filehref);
                }
                closeableHttpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        ssMapUtil.append(source,"noticeContent",text,"publishdate",publishdate);
        String sourceJson=JSON.toJSONString(source);
        System.out.println(sourceJson);
    }


}



































