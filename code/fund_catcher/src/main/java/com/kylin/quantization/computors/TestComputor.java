package com.kylin.quantization.computors;

import com.kylin.quantization.util.ResultUtil;
import com.kylin.quantization.util.RowKeyUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: TestComputor
 * Description:
 * Author: aierxuan
 * Date: 2019-01-11 16:54
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class TestComputor  extends BaseSparkMain{
    public static void main(String[] args) {
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf());
        SQLContext sqlContext=new SQLContext(sparkContext);
        Configuration hbaseConf = getIndexConf();
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRdd = sparkContext.newAPIHadoopRDD(hbaseConf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        JavaRDD<Index> indexRdd = hbaseRdd.map(t -> {
            Result result = t._2;
            String code = RowKeyUtil.getCodeFromIndexRowKey(ResultUtil.row(result));
            Field[] fields = Index.class.getFields();
            Index index = new Index();
            for (Field f : fields) {
                String fieldName = f.getName();
                if ("code".equals(fieldName)) {
                    index.setCode(code);
                    continue;
                }
                Method setMethod = Index.class.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), String.class);
                setMethod.invoke(index, ResultUtil.strVal(result, "baseinfo", fieldName));
            }
            return index;
        });
        DataFrame dataFrame = sqlContext.createDataFrame(indexRdd, Index.class);
        dataFrame.show();
        dataFrame.registerTempTable("index");
        sqlContext.sql("select code,close,timestamp from index where code = 'SZ399006' ").show();


    }



    private static Configuration getIndexConf()  {
        Configuration hconf= HBaseConfiguration.create();
        //需要读取的hbase表名
        String tableName = "index";
        hconf.set(TableInputFormat.INPUT_TABLE, tableName);
        Scan scan = new Scan();
        try {
            hconf.set(TableInputFormat.SCAN, convertScanToString(scan));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hconf;
    }

    public static class Index{
        private String code;
        private String ma10              ;
        private String ma30		 ;
        private String dea		 ;
        private String psy		 ;
        private String ma5		 ;
        private String bias2		 ;
        private String bias3		 ;
        private String bias1		 ;
        private String close		 ;
        private String macd		 ;
        private String timestamp	 ;
        private String wr10		 ;
        private String kdjd		 ;
        private String volume		 ;
        private String dif		 ;
        private String kdjk		 ;
        private String low		 ;
        private String percent		 ;
        private String wr6		 ;
        private String turnoverrate	 ;
        private String rsi1		 ;
        private String rsi2		 ;
        private String rsi3		 ;
        private String psyma		 ;
        private String high		 ;
        private String ub		 ;
        private String lb		 ;
        private String chg		 ;
        private String ma20		 ;
        private String cci		 ;
        private String open		 ;
        private String kdjj              ;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMa10() {
            return ma10;
        }

        public void setMa10(String ma10) {
            this.ma10 = ma10;
        }

        public String getMa30() {
            return ma30;
        }

        public void setMa30(String ma30) {
            this.ma30 = ma30;
        }

        public String getDea() {
            return dea;
        }

        public void setDea(String dea) {
            this.dea = dea;
        }

        public String getPsy() {
            return psy;
        }

        public void setPsy(String psy) {
            this.psy = psy;
        }

        public String getMa5() {
            return ma5;
        }

        public void setMa5(String ma5) {
            this.ma5 = ma5;
        }

        public String getBias2() {
            return bias2;
        }

        public void setBias2(String bias2) {
            this.bias2 = bias2;
        }

        public String getBias3() {
            return bias3;
        }

        public void setBias3(String bias3) {
            this.bias3 = bias3;
        }

        public String getBias1() {
            return bias1;
        }

        public void setBias1(String bias1) {
            this.bias1 = bias1;
        }

        public String getClose() {
            return close;
        }

        public void setClose(String close) {
            this.close = close;
        }

        public String getMacd() {
            return macd;
        }

        public void setMacd(String macd) {
            this.macd = macd;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getWr10() {
            return wr10;
        }

        public void setWr10(String wr10) {
            this.wr10 = wr10;
        }

        public String getKdjd() {
            return kdjd;
        }

        public void setKdjd(String kdjd) {
            this.kdjd = kdjd;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getDif() {
            return dif;
        }

        public void setDif(String dif) {
            this.dif = dif;
        }

        public String getKdjk() {
            return kdjk;
        }

        public void setKdjk(String kdjk) {
            this.kdjk = kdjk;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public String getWr6() {
            return wr6;
        }

        public void setWr6(String wr6) {
            this.wr6 = wr6;
        }

        public String getTurnoverrate() {
            return turnoverrate;
        }

        public void setTurnoverrate(String turnoverrate) {
            this.turnoverrate = turnoverrate;
        }

        public String getRsi1() {
            return rsi1;
        }

        public void setRsi1(String rsi1) {
            this.rsi1 = rsi1;
        }

        public String getRsi2() {
            return rsi2;
        }

        public void setRsi2(String rsi2) {
            this.rsi2 = rsi2;
        }

        public String getRsi3() {
            return rsi3;
        }

        public void setRsi3(String rsi3) {
            this.rsi3 = rsi3;
        }

        public String getPsyma() {
            return psyma;
        }

        public void setPsyma(String psyma) {
            this.psyma = psyma;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getUb() {
            return ub;
        }

        public void setUb(String ub) {
            this.ub = ub;
        }

        public String getLb() {
            return lb;
        }

        public void setLb(String lb) {
            this.lb = lb;
        }

        public String getChg() {
            return chg;
        }

        public void setChg(String chg) {
            this.chg = chg;
        }

        public String getMa20() {
            return ma20;
        }

        public void setMa20(String ma20) {
            this.ma20 = ma20;
        }

        public String getCci() {
            return cci;
        }

        public void setCci(String cci) {
            this.cci = cci;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getKdjj() {
            return kdjj;
        }

        public void setKdjj(String kdjj) {
            this.kdjj = kdjj;
        }
    }
}
