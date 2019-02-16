package com.kylin.quantization.dao.impl;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.BaseDao;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.DateColumnInterpreter;
import com.kylin.quantization.util.ExceptionTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.coprocessor.ColumnInterpreter;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private Configuration hconfiguration=null;
    @Autowired
    public Map<String,String> conf;

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setHconfiguration(Configuration hconfiguration) {
        this.hconfiguration = hconfiguration;
    }

    @Deprecated
    public HBaseDaoImpl init(){
        logger.info("HBaseDaoImpl is init……");
        Admin admin=null;
        TableName fund=TableName.valueOf("netval");
        try {
            //初始化
            String coprocessClassName = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
            admin= getConn().getAdmin();

            HTableDescriptor htd = admin.getTableDescriptor(fund);
            List<String> coprocessors = htd.getCoprocessors();
            if(!coprocessors.contains(coprocessClassName)){
                logger.info(fund.toString()+"Aggregate Coprocessors 不存在");
                htd.addCoprocessor(coprocessClassName);
                if(admin.isTableEnabled(fund)){
                    logger.info("将"+fund.toString()+"置为disable状态");
                    admin.disableTable(fund);
                }else{
                    logger.info(""+fund.toString()+"已经是disable状态");
                }
                admin.modifyTable(fund, htd);
            }else{
                logger.info(fund.toString()+"Aggregate 已存在");
            }

        } catch (Exception e) {
            logger.error(ExceptionTool.toString(e));
        }finally {
            try {
                if(!admin.isTableEnabled(fund)){
                    logger.info("将"+fund.toString()+"恢复为enable状态");
                    admin.enableTable(fund);
                }else{
                    logger.info(""+fund.toString()+"已经是enable状态");
                }
            } catch (IOException e) {
                logger.error(ExceptionTool.toString(e));
            }
            if(admin!=null){
                try {
                    admin.close();
                } catch (IOException e) {
                    logger.error(ExceptionTool.toString(e));
                }
            }
        }
        return this;
    }

    public Connection getConn() {
        if(conn==null){
            synchronized (this){
                if(conn==null){
                    try {
                        conn = ConnectionFactory.createConnection(hconfiguration);
                    } catch (Exception e) {
                        logger.error(ExceptionTool.toString(e));
                    }
                }
            }
        }
        return conn;
    }
    public <T> T aggregate(HBaseExecutors.AggregateExecutor<T> executor){
        AggregationClient ac = new AggregationClient(hconfiguration);
        T result=null;
        try{
            result=executor.doAgg(ac);
        }catch (Exception e){
            logger.error(ExceptionTool.toString(e));
        } catch (Throwable throwable) {
            logger.error(ExceptionTool.toString(throwable));
        } finally {
            if(ac!=null){
                try {
                    ac.close();
                } catch (IOException e) {
                    logger.error(ExceptionTool.toString(e));
                }
            }

        }
        return result;
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
            logger.error(ExceptionTool.toString(e));
        }finally {
            if(admin!=null){
                try {
                    admin.close();
                } catch (IOException e) {
                    logger.error(ExceptionTool.toString(e));
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
            logger.error(ExceptionTool.toString(e));
        }finally {
            if(table!=null){
                try {
                    table.close();
                } catch (IOException e) {
                    logger.error(ExceptionTool.toString(e));
                }
            }
        }
        return result;
    }

    @Override
    public <T> T scan(String tableName, Scan scan, HBaseExecutors.ScanExecutor<T> executor) {
        return table(tableName,table->{
            ResultScanner scanner = null;
            T result=null;
            try {
                scanner=table.getScanner(scan);
                result=executor.doScan(scanner);
            } catch (Throwable throwable) {
                logger.error(ExceptionTool.toString(throwable));
            }finally {
                if(scanner!=null){
                    scanner.close();
                }
            }
            return result;
        });
    }
    @Override
    public void scanForEach(String tableName,Scan scan,HBaseExecutors.ScanForEachExecutor executor){
        scan(tableName,scan,scanner->{
            while(true){
                Result next = scanner.next();
                if(next==null){
                    break;
                }
                executor.doEach(next);
            }
            return null;
        });

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
                logger.error(ExceptionTool.toString(e));
            }
            return flg;
        });
    }

    @Override
    public boolean createTableIfNotExist(String tableName, String... colums) {
        boolean flg=false;
        if(!existTable(tableName)){
            flg=createTable(tableName,colums);
        }
        return flg;
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
                logger.error(ExceptionTool.toString(e));
            }
            return result;
        });
    }
    @Override
    public boolean existTable(String tableName) {
        return admin(admin->admin.tableExists(TableName.valueOf(tableName)));
    }

    @Override
    public boolean putData(String tableName,String rowKey,String family,String qualifier,String value) {
        return table(tableName,table->{
            boolean flg=false;
            try {
                Put put=new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(family),Bytes.toBytes(qualifier),Bytes.toBytes(value));
                table.put(put);
                flg=true;
            } catch (IOException e) {
                logger.error(ExceptionTool.toString(e));
            }
            return flg;
        });
    }

    @Override
    public boolean putData(String tableName, List<Put> puts) {
        return table(tableName,table->{
            boolean flg=false;
            try {
                table.put(puts);
                flg=true;
            } catch (IOException e) {
                logger.error(ExceptionTool.toString(e));
            }
            return flg;
        });
    }

    @Override
    public Result getData(String tableName, String rowKey) {
        return table(tableName,table->{
            Result result=null;
            try {
                Get get=new Get(Bytes.toBytes(rowKey));
                result= table.get(get);
            } catch (IOException e) {
                logger.error(ExceptionTool.toString(e));
            }
            return result;
        });
    }

    @Override
    public void printResult(Result result,String ... qualifiers){
        List<String> qs=Arrays.asList(qualifiers);
        logger.info("printResult start");
        Cell[] cells = result.rawCells();
        for(Cell c : cells){
            String row = Bytes.toString(c.getRowArray(), c.getRowOffset(), c.getRowLength());
            String family= Bytes.toString(c.getFamilyArray(),c.getFamilyOffset(),c.getFamilyLength());
            String column= Bytes.toString(c.getQualifierArray(),c.getQualifierOffset(),c.getQualifierLength());
            String value = Bytes.toString(c.getValueArray(), c.getValueOffset(), c.getValueLength());
            if(qualifiers!=null && qualifiers.length!=0 ){
                if(qs.contains(column)){
                    logger.info(row+"\t"+family+"\t"+column+"\t"+value);
                }
            }else{
                logger.info(row+"\t"+family+"\t"+column+"\t"+value);
            }
        }
        logger.info("=============================================");
    }


}
