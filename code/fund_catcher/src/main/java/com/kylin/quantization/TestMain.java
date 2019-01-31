package com.kylin.quantization;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;


public class TestMain {
    public static void main(String[] args) throws ParseException {
//        insert(new IndexFundCorr("000457","HS000016",new BigDecimal("214.225")));
        System.out.println(new BigDecimal("3.40亿元（截止至：2018年12月31日）".replaceAll("（.+）","").replaceAll("亿元","")).multiply(new BigDecimal("100000000")));
    }


}
