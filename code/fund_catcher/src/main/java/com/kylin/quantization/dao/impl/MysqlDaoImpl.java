package com.kylin.quantization.dao.impl;

import com.kylin.quantization.dao.MysqlDao;
import com.kylin.quantization.model.IndexFundCorr;
import com.kylin.quantization.service.CatcherService;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * ClassName: MysqlDaoImpl
 * Description:
 * Author: aierxuan
 * Date: 2019-01-25 11:33
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Repository
public class MysqlDaoImpl extends BaseDaoImpl implements MysqlDao{
    public static Logger logger= LoggerFactory.getLogger(MysqlDaoImpl.class);
    @Override
    public boolean dropTable(String tableName) {
        //暂未开发
        return false;
    }

    @Override
    public Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://node2:3306/fund_web?useUnicode=true&characterEncoding=UTF-8";
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
    public <T> T conn(MysqlExecutors.ConnExecutor<T> executor){
        Connection conn = getConn();
        T t=null;
        if(conn==null){
            return t;
        }
        try {
            t=executor.doConn(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(conn!=null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;

    }

    public  int insertIndexFundCorr(IndexFundCorr corr, Connection conn) {
        int i = 0;
        String sql = "insert into INDEX_FUND_CORR  values(?,?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, corr.getFundcode());
            pstmt.setString(2, corr.getIndexcode());
            pstmt.setBigDecimal(3, corr.getCorrelationindex());
            pstmt.setInt(4, corr.getCorrtype());
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public void insertCorrIndex(Map<String, Object> row, Connection conn) {
        String fields = row.keySet().stream().reduce((f1, f2) -> f1 + "," + f2).get();
        Object values = row.values().stream().map(v->v.getClass()==String.class?"'"+v+"'":v).reduce((v1, v2) -> v1 + "," + v2).get();
        String valueStr= ObjectUtils.toString(values);
        StringBuffer sql=new StringBuffer("insert into CORR_INDEX(");
        sql.append(fields);
        sql.append(") values(");
        sql.append(valueStr);
        sql.append(")");
        PreparedStatement preparedStatement=null;
        try {
            logger.info("mysql执行sql："+sql);
            preparedStatement = conn.prepareStatement(sql.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(preparedStatement!=null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void truncateTable(String table, Connection conn) {
        PreparedStatement preparedStatement=null;
        try {
            preparedStatement = conn.prepareStatement("truncate table " + table);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(preparedStatement!=null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
