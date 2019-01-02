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
    private Configuration configuration=null;
    @Autowired
    public Map<String,String> conf;

    public Configuration getConfiguration() {
        if(configuration==null){
            configuration=HBaseConfiguration.create();
        }
        return configuration;
    }

    public Connection getConn() {
        if(conn==null){
            synchronized (this){
                if(conn==null){
                    configuration = getConfiguration();
                    Admin admin=null;
                    TableName fund=TableName.valueOf("netval");
                    try {
                        conn = ConnectionFactory.createConnection(configuration);
                        //初始化
                        String coprocessClassName = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
                        admin= conn.getAdmin();

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
                        e.printStackTrace();
                    }finally {
                        try {
                            if(!admin.isTableEnabled(fund)){
                                logger.info("将"+fund.toString()+"恢复为enable状态");
                                admin.enableTable(fund);
                            }else{
                                logger.info(""+fund.toString()+"已经是enable状态");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(admin!=null){
                            try {
                                admin.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }







        return conn;
    }
    public <T> T aggregate(HBaseExecutors.AggregateExecutor<T> executor){
        logger.info("++++"+configuration);
        AggregationClient ac = new AggregationClient(getConfiguration());
        T result=null;
        try{
            result=executor.doAgg(ac);
        }catch (Exception e){
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if(ac!=null){
                try {
                    ac.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
    public boolean putData(String tableName,String rowKey,String family,String qualifier,String value) {
        return table(tableName,table->{
            boolean flg=false;
            try {
                Put put=new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(family),Bytes.toBytes(qualifier),Bytes.toBytes(value));
                table.put(put);
                flg=true;
            } catch (IOException e) {
                e.printStackTrace();
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
                Get get=new Get(Bytes.toBytes(rowKey));
                result= table.get(get);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        });
    }

    @Override
    public String getNewestNetValDate(String code) {
        String tableName="netval";
        logger.info("getNewestNetValDate start");
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(code));
        Scan scan = new Scan();
        scan.setFilter(filter);
        /*table(tableName,table->{
            ResultScanner scanner = table.getScanner(scan);
            Result result=null;
            Set<String> ids=new HashSet<String>();
            while(true){
                logger.info("while");
                result=scanner.next();
                if(result==null){
                    logger.info("result为null,break");
                    break;
                }
                logger.info(Bytes.toString(result.getRow()));
                ids.add(Bytes.toString(result.getRow()));
            }
            logger.info("=====================================================================");
            ids.forEach(s->{
                logger.info(s);
            });
            return null;
        });*/
        Date maxDate = aggregate(agg -> {
            DateColumnInterpreter interpreter = new DateColumnInterpreter();
            Date max = agg.max(TableName.valueOf(tableName), interpreter, scan);
            return max;
        });
        return new SimpleDateFormat("yyyy-MM-dd").format(maxDate);
    }
    private void printResult(Result result){
        logger.info("printResult start");
        Cell[] cells = result.rawCells();
        for(Cell c : cells){
            logger.info(Bytes.toString(c.getRowArray())+" | "+Bytes.toString(c.getFamilyArray())+" | "+Bytes.toString(c.getQualifierArray())+" | "+Bytes.toString(c.getValueArray()));
        }
        logger.info(JSON.toJSONString(cells));
        logger.info("=============================================");
    }


}
