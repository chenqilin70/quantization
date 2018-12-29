package com.kylin.quantization.dao.impl;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.dao.BaseDao;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.ExceptionTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
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
    private Configuration configuration=null;
    @Autowired
    public Map<String,String> conf;


    public Connection getConn() {
        if(conn==null){
            synchronized (this){
                if(conn==null){
                    configuration = HBaseConfiguration.create();
                    Admin admin=null;
                    try {
                        conn = ConnectionFactory.createConnection(configuration);

                        //初始化
                        String coprocessClassName = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
                        admin= conn.getAdmin();
                        TableName fund=TableName.valueOf("fund");
                        admin.disableTable(fund);
                        HTableDescriptor htd = admin.getTableDescriptor(fund);
                        htd.addCoprocessor(coprocessClassName);
                        admin.modifyTable(fund, htd);
                        admin.enableTable(fund);
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
                }
            }
        }







        return conn;
    }
    public <T> T aggregate(HBaseExecutors.AggregateExecutor<T> executor){
        AggregationClient ac = new AggregationClient(configuration);
        T result=null;
        try{
            result=executor.doAgg(ac);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
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
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(code)));
        Scan scan = new Scan();
        scan.setFilter(filter);
        table("netval",table->{
            ResultScanner scanner = table.getScanner(scan);
            Result result=null;
            while(true){
                result=scanner.next();
                if(result==null){
                    break;
                }
                printResult(result);
            }
            return null;
        });
        /*aggregate(agg->{

            agg.max()
            return null;
        });*/
        return null;
    }
    private void printResult(Result result){
        Cell[] cells = result.rawCells();
        for(Cell c : cells){
            System.out.println(Bytes.toString(c.getRowArray())+" | "+Bytes.toString(c.getFamilyArray())+" | "+Bytes.toString(c.getQualifierArray())+" | "+Bytes.toString(c.getValueArray()));
        }
        System.out.println(JSON.toJSONString(cells));
        System.out.println("=============================================");
    }


}
