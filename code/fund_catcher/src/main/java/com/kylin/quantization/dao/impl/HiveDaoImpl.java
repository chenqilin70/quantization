package com.kylin.quantization.dao.impl;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.HiveDao;
import com.kylin.quantization.util.ExceptionTool;
import com.kylin.quantization.util.MapUtil;
import com.kylin.quantization.util.SqlConfigUtil;
import com.kylin.quantization.util.StringReplaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: HiveDaoImpl
 * Description:
 * Author: aierxuan
 * Date: 2019-01-30 11:15
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Repository
public class HiveDaoImpl  extends BaseDaoImpl implements HiveDao {
    private Connection conn=null;
    public static Logger logger= LoggerFactory.getLogger(HiveDaoImpl.class);
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Autowired
    private MapUtil<String,Object> soMapUtil;

    
    @Override
    public boolean dropTable(String tableName) {
        //暂不开发
        return false;
    }

    @Override
    public Connection getConn() {
        if(conn==null){
            synchronized (this){
                if(conn==null){
                    try {
                        Map<String, String> hiveProp = CatcherConfig.proToMap("hive.properties");
                        Class.forName(hiveProp.get("driverName"));
                        conn = DriverManager.getConnection(hiveProp.get("url"),hiveProp.get("user"),hiveProp.get("password"));
                    } catch (Exception e) {
                        logger.error(ExceptionTool.toString(e));
                    }
                }
            }
        }
        return conn;
    }

    @Override
    public List<Map<String, Object>> executeSql(String tab,boolean result,Map<String,String> params) {
        List<Map<String, Object>> list=new ArrayList<>();
        String bizSql = SqlConfigUtil.getBizSql(tab, SqlConfigUtil.HIVE_DOC);
        Connection conn = getConn();
        bizSql = StringReplaceUtil.replace(bizSql, params);
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(bizSql);
            if(result){
                ResultSet resultSet = preparedStatement.executeQuery(bizSql);
                list = convertList(resultSet);

            }else{
                int rows = preparedStatement.executeUpdate(bizSql);
                list.add(soMapUtil.create("rownum",rows));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public List<Map<String, Object>> executeSql(String tab,boolean result) {
        return executeSql(tab,result,ssMapUtil.create());
    }





    /**
     * 封装获得的数据
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private static List<Map<String, Object>> convertList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> rowData = new HashMap<>();// 声明Map
        if (rs != null) {
            ResultSetMetaData md = rs.getMetaData();// 获取键名
            int columnCount = md.getColumnCount();// 获取行的数量
            while (rs.next()) {
                rowData = new HashMap<>();// 声明Map
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));// 获取键名及值
                }
                list.add(rowData);
            }
            logger.info("获得数据的个数:" + list.size());
        }
        return list;
    }


}
