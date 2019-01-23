package com.kylin.quantization;
import com.kylin.quantization.util.RowKeyUtil;
import com.kylin.quantization.util.SqlConfigUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.util.FSHDFSUtils;

import  java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TestMain {
    public static void main(String[] args) throws ParseException {

        System.out.println(RowKeyUtil.getBaseInfoRowKey("000457"));
    }
}
