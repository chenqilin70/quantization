package com.kylin.quantization;

import com.kylin.quantization.util.HttpUtil;
import com.kylin.quantization.util.StringReplaceUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

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
    /*@Test
    public void test(){
        // 建立连接
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://192.168.109.201:9000/hbase");
        conf.set("hbase.zookeeper.quorum", ZKconnect);
        Connection conn = null;
        Admin admin = null;
        try {
//            conn = ConnectionFactory.createConnection(conf);
            admin = new HBaseAdmin(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            boolean exist=admin.tableExists(TableName.valueOf("fund"));
            System.out.println(exist);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/
}
