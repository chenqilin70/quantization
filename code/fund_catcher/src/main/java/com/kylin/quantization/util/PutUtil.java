package com.kylin.quantization.util;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * ClassName: PutUtil
 * Description:
 * Author: aierxuan
 * Date: 2019-01-15 14:39
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class PutUtil {
    public static Put getPut(String rowKey,String family,String qualifier,String val){
        return new Put(Bytes.toBytes(rowKey)).addColumn(Bytes.toBytes(family),Bytes.toBytes(qualifier),Bytes.toBytes(val));
    }
}
