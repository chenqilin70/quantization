package com.kylin.quantization.service;

import com.kylin.quantization.component.CatcherRunner;
import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.MapUtil;
import com.kylin.quantization.util.StringReplaceUtil;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CatcherService {
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private Map<String,String> conf;
    @Autowired
    public static Logger logger= LoggerFactory.getLogger(CatcherService.class);
    public CatcherService() {
        System.out.println("CatcherService is building");
    }


    public List<Map<String, String>> getFundList() {
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
        return result;
    }

    public List<Map<String, String>> getFucndBase(Map<String, String> fund) {
        String detailhtml = HttpUtil.doGet(StringReplaceUtil.replace(conf.get("fund_detail"), fund), null);
        Document html = Jsoup.parse(detailhtml);
        Element table = html.getElementsByClass("info w790").get(0);
        Elements trs = table.getElementsByTag("tr");
        trs.forEach(tr->{
            Elements allElements = tr.getAllElements();
            String th1=allElements.get(1).text();
            String td1=allElements.get(2).text();
            String th2=allElements.get(3).text();
            String td2=allElements.get(4).text();
            logger.info(th1+":"+td1);
            logger.info(th2+":"+td2);
        });
//        System.out.println(info_w790.toString());

        return null;
    }
}
