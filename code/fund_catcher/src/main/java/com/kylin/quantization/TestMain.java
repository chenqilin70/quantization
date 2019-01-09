package com.kylin.quantization;
import org.apache.hadoop.hbase.util.FSHDFSUtils;

import  java.io.File;
import java.io.IOException;


public class TestMain {
    public static void main(String[] args) {
        File f=new File("/OK.txt");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
