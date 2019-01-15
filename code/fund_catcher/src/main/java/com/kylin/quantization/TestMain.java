package com.kylin.quantization;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.util.FSHDFSUtils;

import  java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TestMain {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        Date parse = sf.parse("19700101");
        System.out.println(parse.getTime());
        System.out.println(new Date(0l).toLocaleString());
        System.out.println(Integer.MAX_VALUE);
    }
}
