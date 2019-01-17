package com.kylin.quantization;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.MapUtil;
import com.kylin.quantization.util.RowKeyUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        SonClass.run();
    }
}
