package com.kylin.quantization.util;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * ClassName: ResultUtil
 * Description:
 * Author: aierxuan
 * Date: 2019-01-11 15:00
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class ResultUtil {
    public static byte[] arrVal(Result result,String family,String qualifier){
        return result.getValue(Bytes.toBytes(family),Bytes.toBytes(qualifier));
    }
    public static String strVal(Result result,String family,String qualifier){
        return Bytes.toString(arrVal(result,family,qualifier));
    }
}
