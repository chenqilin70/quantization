package com.kylin.quantization.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.component.CatcherRunner;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.MapUtil;
import com.kylin.quantization.util.StringReplaceUtil;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatcherService {
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private Map<String,String> conf;
    @Autowired
    private Map<String,String> columnCode;
    @Autowired
    private HBaseDao hBaseDao;
    public static Logger logger= LoggerFactory.getLogger(CatcherService.class);


    public List<Map<String, String>> getFundList() {
        logger.info("getFundList start");
        List<Map<String, String>> result=new ArrayList<>();
        String listjson = HttpUtil.doGet(conf.get("fund_list"), null);
        if(StringUtils.isNotBlank(listjson)){
            String[] funds = listjson.replaceAll("var r = \\[\\[", "").replaceAll("\\]\\];", "").split("\\],\\[");
            for(String f:funds){
                List<String> items=Arrays.asList(f.split(",")).stream().map(i->i.replaceAll("\"","")).collect(Collectors.toList());
                Map<String, String> fundMap = ssMapUtil.create("fundcode", items.get(0).trim().replaceAll("\\D",""), "sortpinyin", items.get(1).trim(), "sortname", items.get(2).trim(), "fundtype", items.get(3).trim(), "pinyin", items.get(4).trim());
                result.add(fundMap);
            }
        }
        logger.info("getFundList end");
        return result;
    }

    public void getFundBase(Map<String, String> fund) {
        logger.info("getFucndBase start,fund:"+ JSON.toJSONString(fund));
        String detailhtml = HttpUtil.doGet(StringReplaceUtil.replace(conf.get("fund_detail"), fund), null);
        if(StringUtils.isNotBlank(detailhtml)){
            Document html = Jsoup.parse(detailhtml);
            Element table = html.getElementsByClass("info w790").get(0);
            Elements trs = table.getElementsByTag("tr");
            Map<String,String> fundDetailMap=new HashMap<String,String>();
            trs.forEach(tr->{
                Elements ths = tr.getElementsByTag("th");
                Elements tds = tr.getElementsByTag("td");
                for(int i=0;i<ths.size();i++){
                    Element th = ths.get(i);
                    Element td = tds.get(i);
                    fundDetailMap.put(columnCode.get(th.text().trim()),(  td==null?"":td.text()  ));
                }
            });
            String code = fund.get("fundcode");
            fundDetailMap.putAll(fund);
            List<Put> puts = fundDetailMap.keySet().stream().map(k ->
                 new Put(Bytes.toBytes(code.hashCode() + "_" + code))
                         .addColumn(Bytes.toBytes("baseinfo"), Bytes.toBytes(k), Bytes.toBytes(fundDetailMap.get(k)))
            ).collect(Collectors.toList());
            hBaseDao.putData("fund",puts);
        }
        logger.info("getFucndBase end,fund:"+ JSON.toJSONString(fund));
    }


    public void getNetVal(Map<String, String> fund) {
        logger.info("getNetVal start,fund:"+ JSON.toJSONString(fund));
        //callback=jQuery18305825951889735677_1545638648117&fundCode={fundcode}&pageIndex={pageIndex}&pageSize={pageSize}&startDate=&endDate=&_={now}
        Map<String,String> params=ssMapUtil.create("fundcode",fund.get("fundcode"),"now",new Date().getTime()+"","pageIndex","1","pageSize",Integer.MAX_VALUE+"","callback","jQuery18305825951889735677_1545638648117");
        String netValStr = HttpUtil.doGetWithHead(conf.get("net_val"), params,"head/netval_head.properties");
        netValStr=netValStr.substring(netValStr.indexOf("(")+1,netValStr.lastIndexOf(")"));
        JSONArray json=null;
        try{
            json= JSON.parseObject(netValStr).getJSONObject("Data").getJSONArray("LSJZList");
        }catch (Exception e){
            logger.error("获取净值后解析json报错：fund"+JSON.toJSONString(fund)+",json:"+netValStr,e);
            return;
        }

//        logger.info(json.toJSONString());
        json.forEach(val -> {
            JSONObject valObj = (JSONObject) val;
            String rowkey = fund.get("fundcode") + "_" + valObj.getString("FSRQ");
            rowkey = rowkey.hashCode() + "_" + rowkey;
            final String finalrowkey = rowkey;
            List<Put> puts = valObj.keySet().stream().map(key -> {
                Put put = new Put(Bytes.toBytes(finalrowkey));
                put.addColumn(Bytes.toBytes("baseinfo"), Bytes.toBytes(key), Bytes.toBytes(valObj.getString(key)==null?"":valObj.getString(key)));
                return put;
            }).collect(Collectors.toList());
            hBaseDao.putData("netval",puts);
        });

        logger.info("getNetVal start,fund:"+ JSON.toJSONString(fund));
    }
}
