package com.kylin.quantization.dao;

import com.kylin.quantization.dao.impl.MysqlExecutors;
import com.kylin.quantization.model.IndexFundCorr;

import java.sql.Connection;
import java.util.Map;

/**
 * ClassName: MysqlDao
 * Description:
 * Author: aierxuan
 * Date: 2019-01-25 11:33
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public interface MysqlDao  extends BaseDao{
    Connection getConn();
    <T> T conn(MysqlExecutors.ConnExecutor<T> executor);
    int insertIndexFundCorr(IndexFundCorr indexFundCorr, Connection conn);

    void insertCorrIndex(Map<String, Object> row, Connection conn);
}
