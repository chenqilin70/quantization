package com.kylin.quantization.computors;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.model.IndexFundCorr;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * ClassName: CorrComputor
 * Description:
 * Author: aierxuan
 * Date: 2019-01-24 17:03
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class CorrComputor  extends BaseSparkMain{
    public static void main(String[] args) {
        JavaSparkContext sparkContext=new JavaSparkContext(sparkConf());
        SQLContext sqlContext=new SQLContext(sparkContext);

        Date start=new Date();
        registerHbaseTable("index",getIndexConf(),sparkContext,sqlContext);
        registerHbaseTable("netval",getNetValConf(),sparkContext,sqlContext);
        registerHbaseTable("fund",sparkContext,sqlContext);
        DataFrame resultDF = sql("corr", sqlContext);
        Row[] collect = resultDF.collect();
        Connection conn=getConn();
        for(int k=0;k<collect.length;k++){
            Row row=collect[k];
            IndexFundCorr indexFundCorr = new IndexFundCorr(row.getString(0), row.getString(1), new BigDecimal(row.getDouble(2)));
            System.out.println(JSON.toJSONString(indexFundCorr));
            insert(indexFundCorr,conn);

        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Date end=new Date();
        logger.info("TestComputor is over ,and time is :"+((end.getTime()-start.getTime())/1000.00)+"s");
        /*registerHbaseTable("index",sparkContext,sqlContext);
        sql("index",sqlContext).show();*/


        sparkContext.stop();
    }

    public static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://node2:3306/fund_web";
        String username = "kylin";
        String password = "111111";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static int insert(IndexFundCorr corr,Connection conn) {
        int i = 0;
        String sql = "insert into INDEX_FUND_CORR  values(?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, corr.getFundcode());
            pstmt.setString(2, corr.getIndexcode());
            pstmt.setBigDecimal(3, corr.getCorrelationindex());
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    private static Configuration getIndexConf()  {
        String tableName="index";
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter closeFilter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("close"));
        Scan scan = new Scan()
//                .setStartRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000300", "19491001")))
//                .setStopRow(Bytes.toBytes(RowKeyUtil.getIndexRowkey("SH000300", "20190125")))
                .setFilter(closeFilter);

        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }
    private static Configuration getNetValConf()  {
        String tableName="netval";
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Filter closeFilter =new QualifierFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("LJJZ"));
        Scan scan = new Scan()
                /*.setStartRow(Bytes.toBytes(RowKeyUtil.getNetValRowKey("161604","1949-10-01")))
                .setStopRow(Bytes.toBytes(RowKeyUtil.getNetValRowKey("161604", "2019-01-18")))*/
                .setFilter(closeFilter);

        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }

}
