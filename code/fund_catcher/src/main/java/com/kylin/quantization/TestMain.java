package com.kylin.quantization;
import com.kylin.quantization.computors.TestComputor;
import com.kylin.quantization.model.IndexFundCorr;
import com.kylin.quantization.util.RowKeyUtil;
import com.kylin.quantization.util.SqlConfigUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.util.FSHDFSUtils;
import org.junit.Test;

import  java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TestMain {
    public static void main(String[] args) throws ParseException {
//        insert(new IndexFundCorr("000457","HS000016",new BigDecimal("214.225")));
        System.out.println(new Date(1275321600000l).toLocaleString());
    }


}
