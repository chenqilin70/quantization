package com.kylin.quantization.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


        Date tomorrow=getTomorrow();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Scan scan= new Scan()
                    .setStopRow(Bytes.toBytes(RowKeyUtil.getNetValRowKey(fundcode,"1949-10-01")))
                    .setStartRow(Bytes.toBytes(RowKeyUtil.getNetValRowKey(fundcode,sf.format(tomorrow))))
                    .setReversed(true)
                    .setFilter(new PageFilter(1));
        String result=hBaseDao.scan("netval",scan,scanner -> {
            Result next = scanner.next();
            String date="";
            if(next!=null){

                String row=Bytes.toString(next.getRow());
                date=RowKeyUtil.getDateFormNetvalRowKey(row);
            }
            return date;
        });
        return result;
    }


    public Object test(){
        Scan scan=new Scan();
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        try {
            scan.setStartRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000016",""+sf.parse("19491001").getTime())));
            scan.setStopRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000016",""+sf.parse("20190116").getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String family="baseinfo";
        logger.info("====================================================");
        hBaseDao.scanForEach("index",scan,r->{
            logger.info(ResultUtil.row(r)+":"+ResultUtil.strVal(r,family,"close"));
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
        logger.info("getIndexVal  start,indexCode:"+indexCode);
        String tableName="index";
        boolean exist = hBaseDao.existTable(tableName);
        if(!exist){
            boolean create = hBaseDao.createTable(tableName, "baseinfo");
            if(!create){
                throw new RuntimeException("hbase创建表index失败！");
            }
        }
        String json = HttpUtil.doGetWithHead(conf.get("index_val"), ssMapUtil.create(
                "symbol", indexCode, "begin", getNewestDate(indexCode), "period", "day", "type", "before", "count"
                , Integer.MAX_VALUE+"", "indicator", "kline,ma,macd,kdj,boll,rsi,wr,bias,cci,psy")
        ,"head/index_val_head.properties");
        JSONObject dataJson = JSON.parseObject(json).getJSONObject("data");
        JSONArray columns = dataJson.getJSONArray("column");
        JSONArray items = dataJson.getJSONArray("item");
        for(int i=0;i<items.size();i++){
            JSONArray item = items.getJSONArray(i);
            LinkedList<Put> putList=new LinkedList<>();
            String rowKey=RowKeyUtil.getIndexRowkey(indexCode,item.getString(0));

            for(int j=0;j<columns.size();j++){
                String qualifier = columns.getString(j);
                String val = item.getString(j);
                putList.add(PutUtil.getPut(rowKey,"baseinfo",qualifier,val==null?"":val));
            }
            logger.info("index 入库中，rowkey："+rowKey);
            boolean isPut = hBaseDao.putData(tableName, putList);
            if(!isPut){
                logger.error("index 数据插入失败，已忽略，rowkey:"+rowKey);
            }
        }
        logger.info("getIndexVal  end ,indexCode:"+indexCode);
    }

    /**
     * 根据给定的index，找到其最新netval的日期的后一天
     * @param index
     * @return
     */
    public String getNewestDate(String index) {
        String newestDate= "19491001";

        Date tomorrow=getTomorrow();
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        Scan scan= null;
        try {
            scan = new Scan()
                    .setStartRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey(index,tomorrow.getTime()+"")))
                    .setStopRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey(index,sf.parse(newestDate).getTime()+"")))
                    .setReversed(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result=hBaseDao.scan("index",scan,scanner -> {
            Result next = scanner.next();
            String date="";
            if(next!=null){
                String row=Bytes.toString(next.getRow());
                date=row.substring(row.indexOf("_")+1);
                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.YEAR,Integer.parseInt(date.substring(0,4)));
                calendar.set(Calendar.MONTH,Integer.parseInt(date.substring(4,6))-1);
                calendar.set(Calendar.DATE,Integer.parseInt(date.substring(6)));
                calendar.add(Calendar.DATE,1);
                date=sf.format(calendar.getTime());
            }

            return date;
        });
        return StringUtils.isBlank(result)?newestDate:result;
    }
    public static Date getTomorrow(){
        Calendar c=Calendar.getInstance();
        c.add(Calendar.DATE,1);
        Date tomorrow=c.getTime();
        return tomorrow;
    }

    public void getReport() {
        /*Scan scan=new Scan().setFilter(new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("fundcode")));
        hBaseDao.scan("fund",scan,scanner -> {

            return null;
        });*/
        String reportListUrl = conf.get("report_list");
        //?1=1&fundCode=161604&reportTypeCode=FB030&limit=20&start=20
        String reportListStr = HttpUtil.doGet(reportListUrl, ssMapUtil.create(
                "1", "1",
                "fundCode", "161604",
                "reportTypeCode", "FB030",
                "limit",""+Integer.MAX_VALUE,
                "start","0"
        ));
        Document reportListDoc = Jsoup.parse(reportListStr);
        Elements dds = reportListDoc.getElementsByClass("dd");
        Elements ccs= reportListDoc.getElementsByClass("cc");
        dds.forEach(i->{
            logger.info("=="+i.text());
        });
        ccs.forEach(i->{
            logger.info("=="+i.text());
        });
    }
}
