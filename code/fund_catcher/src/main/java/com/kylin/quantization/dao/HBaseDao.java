package com.kylin.quantization.dao;

import com.kylin.quantization.dao.impl.HBaseExecutors;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import java.util.List;

/**
 * ClassName: HBaseDao
 * Description:
 * Author: aierxuan
 * Date: 2018-12-21 18:16
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public interface HBaseDao extends BaseDao{
    boolean createTable(String tableName,String ... colums);
    Connection getConn();
    <T> T admin(HBaseExecutors.AdminExecutor<T> executor);
    <T> T table(String tableName,HBaseExecutors.TableExecutor<T> executor);
    boolean existTable(String tableName);
    boolean putData(String tableName,String rowKey,String family,String qualifier,String value);
    boolean putData(String tableName,List<Put> puts);
    Result getData(String tableName, String rowKey);

}
