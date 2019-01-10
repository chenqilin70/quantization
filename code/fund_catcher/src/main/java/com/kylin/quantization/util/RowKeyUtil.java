package com.kylin.quantization.util;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * ClassName: RowKeyUtil
 * Description:
 * Author: aierxuan
 * Date: 2018-12-27 10:52
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class RowKeyUtil {
    public static String getNetValRowKey(String fundcode,String time){
        String key=fundcode+"_"+time;
        key=key.hashCode()+"_"+key;
        return key;
    }


    public static String getBaseInfoRowKey(String fundcode){
        return fundcode.hashCode() + "_" + fundcode;
    }

    public static String getCodeFromRowkey(ImmutableBytesWritable immutableBytesWritable){
        return getCodeFromRowkey(immutableBytesWritable.get());
    }
    public static String getCodeFromRowkey(byte[] bytes){
        String code=Bytes.toString(bytes);
        code=code.substring(code.indexOf("_"));
        code=code.substring(0,code.lastIndexOf("_"));
        code=code.replaceAll("_","");
        return code;
    }
}
