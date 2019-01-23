package com.kylin.quantization;

import com.kylin.quantization.model.Index;
import com.kylin.quantization.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * ClassName: TestCenter
 * Description:
 * Author: aierxuan
 * Date: 2018-12-21 11:24
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class TestCenter {
    private static final String ZKconnect="192.168.109.205:2181,192.168.109.204:2181,192.168.109.203:2181";
    private static MapUtil<String,String> ssMapUtil=new MapUtil<>();
    @Test
    public void test(){
        int pageSize=20;
        int index=0;
        while(true){
            String reportListUrl = "http://fund.csrc.gov.cn/web/list_page.upload_info_console";
            //?1=1&fundCode=161604&reportTypeCode=FB030&limit=20&start=20

            String reportListStr = HttpUtil.doGet(reportListUrl, ssMapUtil.create(
                    "1", "1",
                    "fundCode", "161604",
                    "reportTypeCode", "FB030",
                    "limit","20",
                    "start",index+""

            ));
            Document reportListDoc = Jsoup.parse(reportListStr);
            Elements reports = reportListDoc.getElementsByClass("dd");
            Elements treports= reportListDoc.getElementsByClass("aa");
            reports.addAll(treports);
            if(reports==null || reports.size()==0){
                System.out.println("reports `s size is 0,break!");
                break;
            }
            reports.forEach(i->{
                dealDetail(i);
            });
            index+=20;
        }

    }

    private void dealDetail(Element i) {
        Elements a = i.getElementsByTag("a");
        String detailUrl="http://fund.csrc.gov.cn/"+a.get(1).attr("href");
        String detailHttp = HttpUtil.doGet(detailUrl, null);
        System.out.println(detailHttp);
    }
}
