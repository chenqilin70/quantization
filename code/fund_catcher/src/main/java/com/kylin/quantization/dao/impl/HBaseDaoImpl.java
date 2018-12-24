package com.kylin.quantization.dao.impl;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.BaseDao;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.ExceptionTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * ClassName: HBaseDaoImpl
 * Description:
 * Author: aierxuan
 * Date: 2018-12-21 18:16
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Repository
public class HBaseDaoImpl extends BaseDaoImpl implements HBaseDao{
    public static Logger logger= LoggerFactory.getLogger(HBaseDaoImpl.class);
    private Connection conn=null;
    @Autowired
    public Map<String,String> conf;


    public Connection getConn() {
        if(conn==null){
            synchronized (this){
                if(conn==null){
                    Configuration configuration = HBaseConfiguration.create();
//                    configuration.addResource("hbase-site.xml");
                    try {
                        conn = ConnectionFactory.createConnection(configuration);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return conn;
    }

    @Override
    public <T> T admin(HBaseExecutors.AdminExecutor<T> executor) {
        Connection conn= getConn();
        Admin admin=null;
        T result=null;
        try {
            admin= conn.getAdmin();
            result= executor.doAdmin(admin);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(admin!=null){
                try {
                    admin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public <T> T table(String tableName,HBaseExecutors.TableExecutor<T> executor) {
        Connection conn= getConn();
        Table table=null;
        T result=null;
        try {
            table= conn.getTable(TableName.valueOf(tableName));
            result= executor.doTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(table!=null){
                try {
                    table.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public boolean createTable(String tableName, String ... colums) {
        return admin(admin->{
            boolean flg=false;
            try{
                HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
                for (String colum : colums) {
                    tableDescriptor.addFamily(new HColumnDescriptor(colum));
                }
                admin.createTable(tableDescriptor);
                flg=true;
            }catch (Exception e){
                logger.error("",ExceptionTool.toString(e));
            }
            return flg;
        });
    }
    @Override
    public boolean dropTable(String tableName) {
        return admin(admin->{
            boolean result=false;
            try{
                admin.disableTable(TableName.valueOf(tableName));
                admin.deleteTable(TableName.valueOf(tableName));
                result=true;
            }catch (Exception e){
                logger.error("", ExceptionTool.toString(e));
            }
            return result;
        });
    }
    @Override
    public boolean existTable(String tableName) {
        return admin(admin->admin.tableExists(TableName.valueOf(tableName)));
    }

    @Override
    public boolean putData(String tableName,String rowKey,String family,String qualifier) {
        return table(tableName,table->{
            boolean flg=false;
            try {
                Put put = new Put(Bytes.toBytes(rowKey));
                Cell cell=CellUtil.createCell(Bytes.toBytes(rowKey),Bytes.toBytes(family),Bytes.toBytes(qualifier));
                put.add(cell);
                table.put(put);
                flg=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return flg;
        });
    }

    @Override
    public Result getData(String tableName, String rowKey) {
        return table(tableName,table->{
            Result result=null;
            try {
                result= table.get(new Get(Bytes.toBytes(rowKey)));
                for (Cell cell : result.rawCells()) {
                    // 打印结果
                    System.out.print(Bytes.toString(CellUtil.cloneFamily(cell)) + ":");
                    System.out.print(Bytes.toString(CellUtil.cloneQualifier(cell)) + "->");
                    System.out.print(Bytes.toString(CellUtil.cloneValue(cell)));
                }

                logger.info("=================="+result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        });
    }
}
