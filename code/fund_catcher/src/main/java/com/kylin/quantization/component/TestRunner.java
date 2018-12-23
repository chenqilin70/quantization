package com.kylin.quantization.component;

import com.kylin.quantization.service.CatcherService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ClassName: TestRunner
 * Description:
 * Author: aierxuan
 * Date: 2018-12-23 9:48
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Component
public class TestRunner  implements ApplicationRunner {
    public static Logger logger= LoggerFactory.getLogger(CatcherService.class);
    private static final String ZKconnect="192.168.109.205:2181,192.168.109.204:2181,192.168.109.203:2181";
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        logger.info("---------");
        /*Configuration conf = HBaseConfiguration.create();
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
            logger.info("---------"+exist);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
