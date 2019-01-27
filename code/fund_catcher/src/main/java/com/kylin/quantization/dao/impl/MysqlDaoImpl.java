package com.kylin.quantization.dao.impl;

import com.kylin.quantization.dao.MysqlDao;
import com.kylin.quantization.model.IndexFundCorr;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

}
