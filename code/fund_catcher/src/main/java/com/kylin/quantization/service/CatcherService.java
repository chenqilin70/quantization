package com.kylin.quantization.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.component.CatcherRunner;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.*;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
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
        JSONArray json=getNetValJson(fund);
        if(json==null){
            return ;
        }
        String fundcode=fund.get("fundcode");
        String zxrq=getZxrq(fundcode);
        json.forEach(val -> {
            JSONObject valObj = (JSONObject) val;
            String rowkey = fundcode + "_" + valObj.getString("FSRQ");
            rowkey = rowkey.hashCode() + "_" + rowkey;
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


    public List<String> getNoNetValCodes() {
        hBaseDao.table("fund",table->{
            //for each fund code
            Scan scan=new Scan();
            Filter fundcodeFilter=new QualifierFilter(CompareFilter.CompareOp.EQUAL,new BinaryComparator(Bytes.toBytes("fundcode")));
            Filter jjqcFilter=new QualifierFilter(CompareFilter.CompareOp.EQUAL,new BinaryComparator(Bytes.toBytes("jjqc")));
            FilterList filterList=new FilterList(FilterList.Operator.MUST_PASS_ONE,fundcodeFilter,jjqcFilter);
            scan.setFilter(filterList);
            ResultScanner scanner = table.getScanner(scan);
            scanner.forEach(f->{
                byte[] fundcode = f.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("fundcode"));
                byte[] jjqc = f.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("jjqc"));
                //filter from netval .
                Filter netvalFilter=new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("_"+fundcode+"_"));
                Scan netvalScan=new Scan().setFilter(netvalFilter);
                hBaseDao.table("netval",nvtable->{
                    ResultScanner nvScanner = nvtable.getScanner(netvalScan);
                    Result next = nvScanner.next();
                    if(next==null){
                        //if not exist then save
                        logger.warn("not exist fundcode:"+fundcode+",jjqc:"+jjqc);
                    }
                    nvScanner.close();
                    return null;
                });

            });
            return null;
        });
        return null;
    }
    public Object test(){
        String codes="213907,006619,000158,161220,519728,006747,163413,005834,006125,006631,160122,180052,006130,161006,000635,202004,100021,006451,003245,006121,006710,161707,270012,006714,006721,202020,006701,004570,005980,091006,006743,006383,006736,400004,006167,006105,501064,000161,006613,006035,006028,551001,161659,004950,100052,519681,006200,400008,128001,041007,398002,160126,519737,161020,100030,000514,519687,485205,161655,541004,000002,006257,100067,162713,270036,161228,006774,519739,005087,006453,006080,006017,006141,006594,002956,006118,001472,006051,006352,091002,001151,006013,002606,161617,161657,005915,240015,519986,519713,519010,006349,005131,006626,000718,005605,002343,051011,006046,006152,581002,000163,202008,041009,006129,519070,202028,006583,160120,005362,519036,161602,006767,006316,161660,005979,202026,006556,163408,270016,000154,006202,006688,006350,511030,002002,005199,006154,004817,202024,006358,202022,257031,006712,005856,006220,128012,519182,006419,006413,511081,519724,006639,041005,091008,005850,002954,202109,006763,006655,000114,006031,006668,161215,006642,006772,519689,161653,519067,006420,006156,006561,005933,006709,002253,002613,100036,100023,041001,006079,006271,000842,006727,519719,000459,006716,202006,201001,100027,004864,006750,519009,159962,001153,128005,161662,005195,003823,006359,161218,519691,512270,006788,100059,270017,519734,006250,519996,161661,000159,006748,003822,051014,519684,519703,006140,006281,006544,006603,164903,006720,213908,005060,006580,005389,006685,000108,006155,001066,040027,006072,163403,006081,006454,005132,041011,160107,519707,051010,006771,202018,006643,006122,519693,006526,006627,161656,001002,051106,519071,006715,398012,511080,000012,006560,501063,001853,002012,002050,000162,006218,128008,006052,519695,004816,006014,005934,006652,161663,519701,002953,201002,270011,005941,128002,006687,006153,163416,000731,006384,006377,161021,006414,006481,519705,006036,006557,003244,202010,006221,519301,000858,006126,005196,161654,041004,091001,519990,100017,005916,100019,161018,006030,000795,051001,006034,519992,519994,006632,006669,202016,161621,006614,006298,006018,000140,006654,541001,005930,006593,005363,159965,006728,000470,006391,519088,005471,512830,159963,006452,162606,006168,501069,006700,006555,006733,005086,006270,180011,006751,041002,400012,501311,000076,006713,000155,006638,000472,270015,257041,128006,006351,005929,006199,006078,006050,202106,006696,270020,041008,006179,006744,163410,006584,000164,006292,051201,519699,006618,006773,005833,005970,257021,005855,000160,202012,091003,051016,006313,040032,001831,100033,000157";
        Arrays.asList(codes.split(",")).forEach(c->{
            String baseInfoRowKey = RowKeyUtil.getBaseInfoRowKey(c);
            hBaseDao.table("fund",table->{
                Result result = table.get(new Get(Bytes.toBytes(baseInfoRowKey)));
                hBaseDao.printResult(result,"jjdm","fundtype");
                return null;
            });
        });
//        System.out.println(getNetValJson(ssMapUtil.create("fundcode","213907")).toJSONString());;

        return null;
    }

    private JSONArray getNetValJson(Map<String,String> fund){
        logger.info("getNetVal start,fund:"+ JSON.toJSONString(fund));
        //callback=jQuery18305825951889735677_1545638648117&fundCode={fundcode}&pageIndex={pageIndex}&pageSize={pageSize}&startDate=&endDate=&_={now}
        Map<String,String> params=ssMapUtil.create("fundcode",fund.get("fundcode"),"_",new Date().getTime()+"","pageIndex","1","pageSize",Integer.MAX_VALUE+"","callback","jQuery18305825951889735677_1545638648117");
        String netValStr = HttpUtil.doGetWithHead(conf.get("net_val"), params,"head/netval_head.properties");
        netValStr=netValStr.substring(netValStr.indexOf("(")+1,netValStr.lastIndexOf(")"));
        JSONArray json=null;
        try{
            json= JSON.parseObject(netValStr).getJSONObject("Data").getJSONArray("LSJZList");
        }catch (Exception e){
            logger.error("获取净值后解析json报错：fund"+JSON.toJSONString(fund)+",json:"+netValStr,e);
        }
        return json;
    }
}
