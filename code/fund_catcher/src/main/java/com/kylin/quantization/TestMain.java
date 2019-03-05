package com.kylin.quantization;

import com.kylin.quantization.config.CatcherConfig;
import com.kylin.quantization.util.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;


public class TestMain {
    private static Map<String, String> conf = CatcherConfig.proToMap("conf.properties");
    private static MapUtil<String,String> ssMapUtil=new MapUtil<>();
    public static void main(String[] args) throws ParseException, IOException {
        System.out.println(TestMain.class.getClassLoader().getResource(""));

        /*BigDecimal chicang=new BigDecimal("1");
        BigDecimal zuigaojia=new BigDecimal("100");
        BigDecimal huichecanshu=new BigDecimal("0.08");
        BigDecimal duixianbili=new BigDecimal("0.22");
        BigDecimal huicheshouliancanshu=new BigDecimal("0.7");

        StringBuffer sb=new StringBuffer("[");

        while(true){
            System.out.println(chicang.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +new BigDecimal("100").subtract(zuigaojia).divide(new BigDecimal("100"),4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +zuigaojia.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huichecanshu.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +duixianbili.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huicheshouliancanshu.setScale(4,BigDecimal.ROUND_HALF_UP));

            sb.append("[");
            sb.append(new BigDecimal("100").subtract(zuigaojia).divide(new BigDecimal("100"),4,BigDecimal.ROUND_HALF_UP)+","
                            +chicang.setScale(4,BigDecimal.ROUND_HALF_UP)
                    );
            sb.append("],");

            zuigaojia=zuigaojia.multiply(new BigDecimal("1").subtract(huichecanshu));
            chicang=chicang.multiply(new BigDecimal("1").subtract(duixianbili));
            huichecanshu=huichecanshu.multiply(huicheshouliancanshu);
            if(chicang.doubleValue()<0.05){
                break;
            }


        }

        System.out.println(sb.substring(0,sb.lastIndexOf(","))+"]");*/






        /*for(int i=100;i>0;i--){
            BigDecimal xianjia=new BigDecimal(i);
            BigDecimal xiadiefu = zuigaojia.subtract(xianjia).divide(zuigaojia,4,BigDecimal.ROUND_HALF_UP);
            if(xiadiefu.compareTo(huichecanshu)>=0) {
                zuigaojia=xianjia;
                chicang=chicang.multiply(new BigDecimal("1").subtract(duixianbili));
                huichecanshu=huichecanshu.multiply(huicheshouliancanshu);
            }
            System.out.println(xianjia.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +chicang.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +zuigaojia.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huichecanshu.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +duixianbili.setScale(4,BigDecimal.ROUND_HALF_UP)+"\t"
                    +huicheshouliancanshu.setScale(4,BigDecimal.ROUND_HALF_UP));
        }*/








    }




}



































