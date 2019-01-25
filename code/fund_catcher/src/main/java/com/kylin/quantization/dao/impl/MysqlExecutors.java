package com.kylin.quantization.dao.impl;


import java.sql.Connection;

/**
 * ClassName: MysqlExecutors
 * Description:
 * Author: aierxuan
 * Date: 2019-01-25 11:55
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class MysqlExecutors {
    public static interface ConnExecutor<T>{
        T doConn(Connection conn) throws Exception;
    }
}
