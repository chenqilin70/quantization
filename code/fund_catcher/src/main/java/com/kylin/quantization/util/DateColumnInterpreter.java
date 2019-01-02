package com.kylin.quantization.util;

import com.google.protobuf.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.coprocessor.ColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName: DateColumnInterpreter
 * Description:
 * Author: aierxuan
 * Date: 2018-12-29 17:28
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class DateColumnInterpreter extends ColumnInterpreter<Date,Date,Message,Message,Message> {

    @Override
    public Date getValue(byte[] colFamily, byte[] colQualifier, Cell c) throws IOException {
        System.out.println("getValue……"+Bytes.toString(colFamily)+","+Bytes.toString(colQualifier));
        String val = Bytes.toString(c.getValueArray());
        Date result=null;
        if(StringUtils.isNotBlank(val)){
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            try {
                result=sf.parse(val);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    @Override
    public Date add(Date l1, Date l2) {
        return null;
    }

    @Override
    public Date getMaxValue() {
        return null;
    }

    @Override
    public Date getMinValue() {
        return null;
    }

    @Override
    public Date multiply(Date o1, Date o2) {
        return null;
    }

    @Override
    public Date increment(Date o) {
        return null;
    }

    @Override
    public Date castToReturnType(Date o) {
        return null;
    }

    @Override
    public int compare(Date l1, Date l2) {
        System.out.println("compare……");
        long result=l1.getTime()-l2.getTime();
        return result>0?1:result<0?-1:0;
    }

    @Override
    public double divideForAvg(Date o, Long l) {
        return 0;
    }

    @Override
    public Message getRequestData() {
        return null;
    }

    @Override
    public void initialize(Message msg) {
    }

    @Override
    public Message getProtoForCellType(Date date) {
        return null;
    }

    @Override
    public Date getCellValueFromProto(Message message) {
        return null;
    }

    @Override
    public Message getProtoForPromotedType(Date date) {
        return null;
    }

    @Override
    public Date getPromotedValueFromProto(Message message) {
        return null;
    }

    @Override
    public Date castToCellType(Date response) {
        return null;
    }
}
