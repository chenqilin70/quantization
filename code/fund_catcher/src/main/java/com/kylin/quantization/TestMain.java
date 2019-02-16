package com.kylin.quantization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.util.*;
import org.apache.hadoop.hbase.client.Put;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;


public class TestMain {
    public static void main(String[] args) throws ParseException, UnsupportedEncodingException {
        Map<String, String> conf = CatcherConfig.proToMap("conf.properties");
        MapUtil<String, String> ssMapUtil = new MapUtil<>();
        List<String> wds = Arrays.asList("kzz_lb,kzz_mx,kzz_fxlx,kzz_zqh,kzz_czyt,kzz_zyrq,kzz_hssh".split(","));
        String encode = "http://dcfm.eastmoney.com/em_mutisvcexpandinterface/api/js/get";
        String result = HttpUtil.doGet(encode, ssMapUtil.create("js","{data:(x)}","type","KZZ_LB2.0","token","70f12f2f4f091e459a279469fe49eca5","st","ZQNEW"
                ,"sr","1","p","1","ps","1000","rt","51676339"));
        JSONArray bonds = JSON.parseObject(result).getJSONArray("data");
        bonds.stream().map(b->((JSONObject)b).getString("BONDCODE")).forEach(code->{
            System.out.println(code);
            wds.forEach(w->{
                String url= StringReplaceUtil.replace(conf.get(w),ssMapUtil.create("bondCode",code.trim()));
                String tableName = w.replaceAll("_", "");
                String dataStr = HttpUtil.doGet(url, null);
                JSONArray dataArr = JSON.parseArray(dataStr);
                int index=0;
                List<Put> putList=new ArrayList<>();
                dataArr.forEach(dataObj->{
                    JSONObject datajson=((JSONObject)dataObj);
                    datajson.keySet().forEach(key->{
                        System.out.println(key+","+datajson.getString(key));
                    });
                });

            });
        });


}}
