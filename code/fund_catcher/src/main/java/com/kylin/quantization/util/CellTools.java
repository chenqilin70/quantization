package com.kylin.quantization.util;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.util.Bytes;

public class CellTools {
    public static String val(Cell c){
        return Bytes.toString(c.getValueArray(), c.getValueOffset(), c.getValueLength());
    }
    public static String column(Cell c){
        return Bytes.toString(c.getQualifierArray(),c.getQualifierOffset(),c.getQualifierLength());
    }
    public static String family(Cell c){
        return Bytes.toString(c.getFamilyArray(),c.getFamilyOffset(),c.getFamilyLength());
    }
    public static String row(Cell c){
        return Bytes.toString(c.getRowArray(), c.getRowOffset(), c.getRowLength());
    }
}
