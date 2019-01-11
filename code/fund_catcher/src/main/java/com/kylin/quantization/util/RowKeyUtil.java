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
    /**
     *
     * @param fundcode   161604
     * @param time      2018-12-12
     * @return
     */
    public static String getNetValRowKey(String fundcode,String time){
        String key=fundcode+"_"+time.replaceAll("-","");
        return key;
    }

    public static byte[] getNetValRowKeyArray(String fundcode, String time){
        String key=getNetValRowKey(fundcode,time);
        return Bytes.toBytes(key);
    }


    public static String getBaseInfoRowKey(String fundcode){
        return fundcode.hashCode() + "_" + fundcode;
    }


    public static String getCodeFromRowkey(byte[] bytes){
        String code=Bytes.toString(bytes);
        code=code.substring(0,code.indexOf("_"));
        return code;
    }
}
