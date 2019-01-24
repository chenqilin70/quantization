package com.kylin.quantization;

import com.kylin.quantization.computors.TestComputor;
import com.kylin.quantization.model.Index;
import com.kylin.quantization.model.IndexFundCorr;
import com.kylin.quantization.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.seimicrawler.xpath.JXDocument;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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




}
