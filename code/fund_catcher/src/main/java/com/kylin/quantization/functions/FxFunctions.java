package com.kylin.quantization.functions;

import com.google.common.base.Optional;
import com.kylin.quantization.util.RegexEnum;
import com.kylin.quantization.util.RowKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import  static com.kylin.quantization.computors.FXSort.*;

/**
 * ClassName: FxFunctions
 * Description:
 * Author: aierxuan
 * Date: 2019-01-14 17:30
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class FxFunctions {
    public static class FundFilterFunction implements Function<Tuple2<ImmutableBytesWritable, Result>, Boolean> {

        @Override
        public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
            boolean flg = false;
            byte[] value = tuple._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("fxrq"));
            SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
            if (value != null && value.length != 0) {
                String fxrq = Bytes.toString(value);
                Date fxrqDate = sf.parse(fxrq);
                Calendar ago = Calendar.getInstance();
                ago.add(Calendar.YEAR, -LEAST_YEARS);
                if (ago.getTime().getTime() >= fxrqDate.getTime()) {
                    Date startDate = sf.parse(START_DATE);
                    if(startDate.getTime()>=fxrqDate.getTime()){
                        flg = true;
                    }
                }
            }
            return flg;
        }
    }

    public static class NetvalFilterFunction implements Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>{

        @Override
        public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
            String ljjz=Bytes.toString(tuple2._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("LJJZ")));
            Matcher matcher=null;
            if(StringUtils.isNotBlank(ljjz)){
                Pattern pattern=Pattern.compile(RegexEnum.NET_VAL_REG.val());
                matcher=pattern.matcher(ljjz);
            }
            String fsrq = Bytes.toString(tuple2._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("FSRQ")));

            return StringUtils.isNotBlank(ljjz) &&  matcher.matches() && isBetween(START_DATE,END_DATE,fsrq);
        }
        public static Boolean isBetween(String start, String end,String target) throws ParseException {
            String[] patterns=new String[]{"yyyyMMdd","yyyy-MM-dd","yyyy年MM月dd日"};
            Date startDate = DateUtils.parseDate(start, patterns);
            Date endDate = DateUtils.parseDate(end, patterns);
            Date targetDate = DateUtils.parseDate(target, patterns);
            if(startDate.getTime()<=targetDate.getTime() && targetDate.getTime()<=endDate.getTime()){
                return true;
            }else{
                return false;
            }
        }
    }

    public static class CodeNetvalRddFunction implements PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, BigDecimal>{
        @Override
        public Tuple2<String, BigDecimal> call(Tuple2<ImmutableBytesWritable, Result> t) throws Exception {
            String code= RowKeyUtil.getCodeFromRowkey(t._2.getRow());
            String ljjz=Bytes.toString(t._2.getValue(Bytes.toBytes("baseinfo"), Bytes.toBytes("LJJZ")));
            BigDecimal netval=new BigDecimal(ljjz);
            return new Tuple2<String, BigDecimal>(code,netval);
        }
    }

    public static class FxSortFunction implements  PairFunction<Tuple2<String, Tuple2<BigDecimal, Optional<BigDecimal>>>, BigDecimal, Tuple2<String,BigDecimal>>{

        @Override
        public Tuple2<BigDecimal, Tuple2<String, BigDecimal>> call(Tuple2<String, Tuple2<BigDecimal, Optional<BigDecimal>>> tuple) throws Exception {
            BigDecimal result1=tuple._2._1.divide(tuple._2._2.orNull(), 4, BigDecimal.ROUND_HALF_UP);
            Tuple2<String, BigDecimal> result2 = new Tuple2<>(tuple._1, tuple._2._1.divide(tuple._2._2.orNull(), 4, BigDecimal.ROUND_HALF_UP));
            return new Tuple2<BigDecimal, Tuple2<String, BigDecimal>>(result1,result2);
        }
    }




}
