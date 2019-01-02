package com.kylin.quantization.dao.impl;

import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;

import java.io.IOException;

/**
 * ClassName: HBaseExecutor
 * Description:
 * Author: aierxuan
 * Date: 2018-12-23 14:00
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public interface  HBaseExecutors {
    public static interface AdminExecutor<T>{
        T doAdmin(Admin admin) throws IOException;
    }
    public static interface TableExecutor<T>{
        T doTable(Table admin) throws IOException;
    }
    public static interface AggregateExecutor<T>{
        T doAgg(AggregationClient aggClient) throws Throwable;
    }


}
