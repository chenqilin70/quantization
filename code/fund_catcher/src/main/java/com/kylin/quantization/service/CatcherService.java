package com.kylin.quantization.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.dao.HiveDao;
import com.kylin.quantization.dao.MysqlDao;
import com.kylin.quantization.model.Fund;
import com.kylin.quantization.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    @Autowired
    private MysqlDao mysqlDao;
    @Autowired
    private HiveDao hiveDao;
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
        List<Map<String, Object>> result = hiveDao.executeSql("corr_index", true);
        logger.info(JSON.toJSONString(result));
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
        createHbaseTableIfNotExists(tableName);
        //symbol=SZ128038&begin=1551420485789&period=day&type=before&count=-142&indicator=kline,ma,macd,kdj,boll,rsi,wr,bias,cci,psy,pe,pb,ps,pcf,market_capital,agt,ggt,balance
        String json = HttpUtil.doGetWithHead(conf.get("kline_val"), ssMapUtil.create(
                "symbol", indexCode, "begin", getNewestDate(indexCode,"index"), "period", "day", "type", "before", "count"
                , Integer.MAX_VALUE+"", "indicator", "kline,ma,macd,kdj,boll,rsi,wr,bias,cci,psy")
        , "head/kline_val_head.properties");
        dealKlineData(tableName,json,indexCode);
        logger.info("getIndexVal  end ,indexCode:"+indexCode);
    }

    public void getStockVal(String stockCode) {
        logger.info("getStockVal  start,stockCode:"+stockCode);
        String tableName="stock";
        createHbaseTableIfNotExists(tableName);
        String json = HttpUtil.doGetWithHead(conf.get("kline_val"), ssMapUtil.create(
                "symbol", stockCode, "begin", getNewestDate(stockCode,"stock"), "period", "day", "type", "before", "count"
                , Integer.MAX_VALUE+"", "indicator", "kline,ma,macd,kdj,boll,rsi,wr,bias,cci,psy,pe,pb,ps,pcf,market_capital,agt,ggt,balance")
                , "head/kline_val_head.properties");
        dealKlineData(tableName,json,stockCode);
        logger.info("getStockVal  end ,indexCode:"+stockCode);
    }
    public void dealKlineData(String tableName,String json,String code){
        JSONObject dataJson = JSON.parseObject(json).getJSONObject("data");
        JSONArray columns = dataJson.getJSONArray("column");
        JSONArray items = dataJson.getJSONArray("item");
        for(int i=0;i<items.size();i++){
            JSONArray item = items.getJSONArray(i);
            LinkedList<Put> putList=new LinkedList<>();
            String rowKey=RowKeyUtil.getKlineRowkey(code,item.getString(0));

            for(int j=0;j<columns.size();j++){
                String qualifier = columns.getString(j);
                String val = item.getString(j);
                putList.add(PutUtil.getPut(rowKey,"baseinfo",qualifier,val==null?"":val));
            }
            logger.info(tableName+" 入库中，rowkey："+rowKey);
            boolean isPut = hBaseDao.putData(tableName, putList);
            if(!isPut){
                logger.error(tableName+" 数据插入失败，已忽略，rowkey:"+rowKey);
            }
        }
    }


    public void createHbaseTableIfNotExists(String tableName){
        boolean exist = hBaseDao.existTable(tableName);
        if(!exist){
            boolean create = hBaseDao.createTable(tableName, "baseinfo");
            if(!create){
                throw new RuntimeException("hbase创建表index失败！");
            }
        }
    }


    /**
     * 根据给定的index，找到其最新netval的日期的后一天
     * @param index
     * @return
     */
    public String getNewestDate(String index,String indexOrStock) {
        String newestDate= "19491001";

        Date tomorrow=getTomorrow();
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        Scan scan= null;
        try {
            scan = new Scan()
                    .setStartRow(Bytes.toBytes(RowKeyUtil.getKlineRowkey(index,tomorrow.getTime()+"")))
                    .setStopRow(Bytes.toBytes(RowKeyUtil.getKlineRowkey(index,sf.parse(newestDate).getTime()+"")))
                    .setReversed(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result=hBaseDao.scan(indexOrStock,scan,scanner -> {
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
        try {
            newestDate=sf.parse(  StringUtils.isBlank(result)?newestDate:result  ).getTime()+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newestDate;
    }
    public static Date getTomorrow(){
        Calendar c=Calendar.getInstance();
        c.add(Calendar.DATE,1);
        Date tomorrow=c.getTime();
        return tomorrow;
    }

    public void getReport() {
        logger.info("getReport start");
        /*Scan scan=new Scan().setFilter(new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("fundcode")));
        hBaseDao.scan("fund",scan,scanner -> {

            return null;
        });*/
        String reportListUrl = conf.get("report_list");
        //?1=1&fundCode=161604&reportTypeCode=FB030&limit=20&start=20
        String reportListStr = HttpUtil.doGet(reportListUrl, ssMapUtil.create(
                "1", "1",
                "fundCode", "000001",
                "reportTypeCode", "FB030"
        ));
        logger.info(reportListStr);
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

    public void fundHbaseToMysql() {
        hBaseDao.scanForEach("fund",new Scan(),result -> {
            Method[] declaredMethods = Fund.class.getDeclaredMethods();
            List<Method> methods=Arrays.asList(declaredMethods);
            methods = methods.stream().filter(m -> m.getName().startsWith("set")).collect(Collectors.toList());
            List<String> fields = methods.stream().map(m -> m.getName().replaceAll("set", "").toLowerCase()).collect(Collectors.toList());
            mysqlDao.conn(conn -> {
                StringBuffer fieldSql=new StringBuffer("rowkey,");
                StringBuffer valuesSql=new StringBuffer(ResultUtil.row(result)+",");
                fields.forEach(f->{
                    fieldSql.append(f+",");
                    valuesSql.append("'"+ResultUtil.strVal(result,"baseinfo",f)+"',");
                });
                String sql="insert into FUND("+fieldSql.substring(0,fieldSql.lastIndexOf(","))+") values("+valuesSql.substring(0,valuesSql.lastIndexOf(","))+")";
                PreparedStatement pstmt=null;
                try {
                    logger.info("执行sql："+sql);
                    pstmt = (PreparedStatement) conn.prepareStatement(sql);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }finally {
                    if(pstmt!=null){
                        pstmt.close();
                    }
                }
                return null;
            });
        });

    }

    public void corrIndex() {
        logger.info("corrIndex start");
        List<Map<String, Object>> corr_index = hiveDao.executeSql("corr_index", true);
        mysqlDao.conn(conn -> {
            mysqlDao.truncateTable("CORR_INDEX",conn);
            corr_index.forEach(row->mysqlDao.insertCorrIndex(row,conn));
            return null;
        });
        logger.info("corrIndex end");
    }

    public void stockNotice(String stockcode) {
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



    private  void dealDetail(String href,Map<String,String> source) {
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
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ssMapUtil.append(source,"noticeContent",text,"publishdate",publishdate,"htmlUrl",href,"fileUrl",filehref,"createtime",sf.format(new Date()));
        String sourceJson=JSON.toJSONString(source);
        ESUtil.putData(sourceJson,"stock_notice");
    }
}
