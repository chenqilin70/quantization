package com.kylin.quantization.thread;


import com.alibaba.fastjson.JSON;
import com.kylin.quantization.dao.MysqlDao;
import com.kylin.quantization.dao.impl.MysqlDaoImpl;
import com.kylin.quantization.model.IndexFundCorr;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Row;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * ClassName: CorrTask
 * Description:
 * Author: aierxuan
 * Date: 2019-01-27 10:36
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class CorrTask  extends BaseRecursiveTask<Row,Object>{
    public CorrTask(List<Row> datas, int THRESHOLD_NUM) {
        super(datas, THRESHOLD_NUM);
    }

    @Override
    public Object run(List<Row> perIncrementList) {
        MysqlDao mysqlDao=new MysqlDaoImpl();
        Connection conn=mysqlDao.getConn();
        for(int k=0;k<perIncrementList.size();k++){
            Row row=perIncrementList.get(k);
            String corr="";
            try {
                corr=row.getDouble(2)+"";
            }catch (Exception e){
                System.out.println("getDouble error,row:"+row+"  ,exception:"+e.getMessage());
            }
            IndexFundCorr indexFundCorr = new IndexFundCorr(row.getString(0), row.getString(1), StringUtils.isBlank(corr)?null:new BigDecimal(corr),row.getInt(3));
            System.out.println(JSON.toJSONString(indexFundCorr));
            mysqlDao.insertIndexFundCorr(indexFundCorr,conn);
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Object reduce(Object leftResult, Object rightResult) {
        return null;
    }

    @Override
    public BaseRecursiveTask getBaseRecursiveTask(List<Row> dataList, int THRESHOLD_NUM) {
        return new CorrTask(dataList,THRESHOLD_NUM);
    }
}
