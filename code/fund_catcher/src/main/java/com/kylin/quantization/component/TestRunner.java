package com.kylin.quantization.component;

import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.service.CatcherService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
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
    public static Logger logger= Logger.getLogger(TestRunner.class);
    @Autowired
    private HBaseDao hBaseDao;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        boolean flg=hBaseDao.createTable("netval","baseinfo");
        logger.info(flg+"-------------------");
    }
}
