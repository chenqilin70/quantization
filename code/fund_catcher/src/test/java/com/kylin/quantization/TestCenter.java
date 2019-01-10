package com.kylin.quantization;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.MapUtil;
import com.kylin.quantization.util.StringReplaceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

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
        System.out.println(new BigDecimal("3").pow(2));
        /*System.out.println(Integer.MAX_VALUE);
        String url="http://api.fund.eastmoney.com/f10/lsjz?callback=jQuery18305825951889735677_1545638648117&fundCode={fundcode}&pageIndex=1&pageSize=10000000&startDate=&endDate=&_="+new Date().getTime();
        String netValStr = HttpUtil.doGetWithHead(StringReplaceUtil.replace(url,ssMapUtil.create("fundcode","161604")), null,"head/netval_head.properties");
        netValStr=netValStr.substring(netValStr.indexOf("(")+1,netValStr.lastIndexOf(")"));
        System.out.println(JSON.parseObject(netValStr).getJSONObject("Data").getJSONArray("LSJZList").size());*/

    }
}
