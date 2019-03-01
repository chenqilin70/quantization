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
        /*CloseableHttpResponse closeableHttpResponse = HttpUtil.doGetFile("http://pdf.dfcfw.com/pdf/H2_AN201902271300609647_1.pdf");
        closeableHttpResponse.getEntity().getContent();
        CloseableHttpResponse closeableHttpResponse1 = HttpUtil.doGetFile("http://pdf.dfcfw.com/pdf/H2_AN201902271300609647_1.pdf");
        FileUtils.copyInputStreamToFile(closeableHttpResponse1.getEntity().getContent(),new File("E:\\1.pdf"));*/


        String filehrefs="http://pdf.dfcfw.com/pdf/H2_AN201602030013375205_1.doc;http://pdf.dfcfw.com/pdf/H2_AN201807031162491366_1.doc";
        for(String filehref:filehrefs.split(";")){
            CloseableHttpResponse closeableHttpResponse = HttpUtil.doGetFile(filehref);
            HttpEntity entity = closeableHttpResponse.getEntity();
            String text="";
            try {
                if(filehref.endsWith("pdf")){
                    text= PDFUtil.getText(entity.getContent());
                }else if(filehref.endsWith("txt")){
                    text= EntityUtils.toString(entity,"gbk");
                }else if(filehref.endsWith("doc")){
                    InputStream inputStream = entity.getContent();
                    HWPFDocument hwpfDocument=null;
                    try{
                        hwpfDocument = new HWPFDocument(inputStream);
                        text=hwpfDocument.getText().toString();
                    }catch (IllegalArgumentException e){
                        System.out.println("========================");
                        CloseableHttpResponse tempResponse=null;
                        XWPFDocument xdoc=null;
                        try{
                            closeableHttpResponse.close();
                            tempResponse= HttpUtil.doGetFile(filehref);
                            xdoc= new XWPFDocument(tempResponse.getEntity().getContent());
                            new XWPFDocument();
                                /*POIXMLTextExtractor extractor = new XWPFWordExtractor(xdoc);

                                text = extractor.getText();*/


                            List<XWPFParagraph> paragraphs = new ArrayList<XWPFParagraph>();
                            // 列表外段落
                            paragraphs.addAll(xdoc.getParagraphs());
                            // 列表内段落
                            List<XWPFTable> tables = xdoc.getTables();
                            for (XWPFTable table : tables) {
                                List<XWPFTableRow> rows = table.getRows();
                                for (XWPFTableRow row : rows) {
                                    List<XWPFTableCell> cells = row.getTableCells();
                                    for (XWPFTableCell cell : cells) {
                                        paragraphs.addAll(cell.getParagraphs());
                                    }
                                }
                            }
                            text=paragraphs.stream().map(p->p.getText()+"\n").reduce((p1,p2)->p1+p2).get();

                        }catch (Exception ex){
                            System.out.println(ExceptionTool.toString(ex));
                        }finally {
                            if(tempResponse!=null){
                                tempResponse.close();
                            }
                            if(xdoc!=null){
                                xdoc.close();
                            }
                        }

                    }finally {
                        if(hwpfDocument!=null){
                            hwpfDocument.close();
                        }
                    }

                }else {
                    System.out.println("文件格式无法解析：href:"+filehref);
                }

            } catch (Exception e) {
                System.out.println("解析entity错误："+ExceptionTool.toString(e));
            }finally {
                if(closeableHttpResponse!=null){
                    closeableHttpResponse.close();
                }
            }
            System.out.println(text);
        }







    }




}



































