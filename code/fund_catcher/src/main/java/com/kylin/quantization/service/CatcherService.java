package com.kylin.quantization.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.kylin.quantization.component.CatcherRunner;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.*;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    /**
     * fundlist-update,notinfundlist-noOpt
     * @param fund
     */
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
                 new Put(Bytes.toBytes(RowKeyUtil.getBaseInfoRowKey(code)))
                         .addColumn(Bytes.toBytes("baseinfo"), Bytes.toBytes(k), Bytes.toBytes(fundDetailMap.get(k)))
            ).collect(Collectors.toList());
            hBaseDao.putData("fund",puts);
        }
        logger.info("getFucndBase end,fund:"+ JSON.toJSONString(fund));
    }


    public void getNetVal(Map<String, String> fund) {
        logger.info("getNetVal start,fund:"+ JSON.toJSONString(fund));
        String fundcode=fund.get("fundcode");
        String zxrq=getZxrq(fundcode);
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        String startDate="";
        try {
            Date date = sf.parse(zxrq);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE,1);
            startDate=sf.format(calendar.getTime());
        } catch (ParseException e) {
            logger.warn("该fund没有zxrq，startDate将以空字符串处理，fund:"+JSON.toJSONString(fund));
        }

        //&startDate=2019-01-02&endDate=2019-01-04
        JSONArray json=getNetValJson(fund,startDate);
        if(json==null){
            return ;
        }
        json.forEach(val -> {
            JSONObject valObj = (JSONObject) val;
            String rowkey = RowKeyUtil.getNetValRowKey(fundcode,valObj.getString("FSRQ"));
            final String finalrowkey = rowkey;
            List<Put> puts = valObj.keySet().stream().map(key -> {
                Put put = new Put(Bytes.toBytes(finalrowkey));
                put.addColumn(Bytes.toBytes("baseinfo"), Bytes.toBytes(key), Bytes.toBytes(valObj.getString(key)==null?"":valObj.getString(key)));
//                System.out.println("key:"+key+",val:"+valObj.getString(key));
                return put;
            }).collect(Collectors.toList());
            hBaseDao.putData("netval",puts);

        });

        logger.info("getNetVal end,fund:"+ JSON.toJSONString(fund));
    }

    public String getZxrq(String fundcode) {
        return hBaseDao.table("fund",table->{
            String result="";
            Get get = new Get(Bytes.toBytes(RowKeyUtil.getBaseInfoRowKey(fundcode)));
            get= get.addColumn(Bytes.toBytes("baseinfo"),Bytes.toBytes("zxrq"));
            Result r = table.get(get);
            Cell[] cells = r.rawCells();
            if(cells!=null && cells.length!=0){
                result = CellTools.val(cells[0]);
            }
            return result;
        });
    }


    public Object test(){
        /*Scan scan=new Scan()
                .setStartRow(RowKeyUtil.getNetValRowKeyArray("161604","2018-12-01"))
                .setStopRow(RowKeyUtil.getNetValRowKeyArray("161604","2018-12-28"));
        hBaseDao.scanForEach("netval",scan,result -> {
            String fsrq = ResultUtil.strVal(result, "baseinfo", "FSRQ");
            String ljjz = ResultUtil.strVal(result, "baseinfo", "LJJZ");
            logger.info(ResultUtil.row(result)+"=="+fsrq+"==>"+ljjz);
        });*/
       Scan scan=new Scan();
        Filter filter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("LJJZ"));
        Filter filter3 = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("FSRQ"));
        FilterList qualifierFilter = new FilterList(FilterList.Operator.MUST_PASS_ONE, filter2, filter3);
        scan.setFilter(qualifierFilter);
       hBaseDao.table("netval",table->{
           ResultScanner scanner = table.getScanner(scan);
           while(true){
               Result next = scanner.next();
               if(next==null){
                   break;
               }
               String ljjz = ResultUtil.strVal(next, "baseinfo", "LJJZ");
               Pattern pattern=Pattern.compile("\\d+\\.\\d+");
               Matcher matcher=pattern.matcher(ljjz);
               if(StringUtils.isNotBlank(ljjz) && !matcher.matches() ){
                   logger.info("不是数字也不是空："+ljjz);
               }
           }
           return null;
       });


        return null;
    }

    private JSONArray getNetValJson(Map<String,String> fund,String startdate){
        logger.info("getNetVal start,fund:"+ JSON.toJSONString(fund));
        //callback=jQuery18305825951889735677_1545638648117&fundCode={fundcode}&pageIndex={pageIndex}&pageSize={pageSize}&startDate=&endDate=&_={now}
        Map<String,String> params=ssMapUtil.create("fundcode",fund.get("fundcode")
                ,"_",new Date().getTime()+"","pageIndex","1","pageSize",Integer.MAX_VALUE+""
                ,"callback","jQuery18305825951889735677_1545638648117","startDate",startdate,"endDate","");
        String netValStr = HttpUtil.doGetWithHead(conf.get("net_val"), params,"head/netval_head.properties");
        netValStr=netValStr.substring(netValStr.indexOf("(")+1,netValStr.lastIndexOf(")"));
        JSONArray json=null;
        try{
            json= JSON.parseObject(netValStr).getJSONObject("Data").getJSONArray("LSJZList");
        }catch (Exception e){
            logger.error("获取netval后解析json报错：fund"+JSON.toJSONString(fund)+",json:"+netValStr,e);
        }
        return json;
    }

    public void flushAndCompact(String tableName) {
        hBaseDao.admin(admin -> {
            admin.flush(TableName.valueOf(tableName));
            admin.majorCompact(TableName.valueOf(tableName));
            return null;
        });
    }

    /**
     * symbol:SH000001
     * begin:1547529120479
     * period:day
     * type:before
     * count:-142
     * indicator:kline,ma,macd,kdj,boll,rsi,wr,bias,cci,psy
     * @param indexCode
     */
    public void getIndexVal(String indexCode) {
        String json = HttpUtil.doGet(conf.get("index_val"), ssMapUtil.create(
                "symbol", indexCode, "begin", "1547529120479", "period", "day", "type", "before", "count", "-142", "indicator", "kline,ma,macd,kdj,boll,rsi,wr,bias,cci,psy"));
        JSONObject dataJson = JSON.parseObject(json).getJSONObject("data");
        JSONArray columns = dataJson.getJSONArray("column");
        JSONArray items = dataJson.getJSONArray("item");
        columns.forEach(c->{System.out.print(c+",class:"+c.getClass());});

    }
}
