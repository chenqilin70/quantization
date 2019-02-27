package com.kylin.quantization.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HdfsUtil {
    public static FileSystem getFs(){
        FileSystem fs = null;
        Configuration conf = null;

        // 方法一，本地有配置文件，直接获取配置文件（core-site.xml，hdfs-site.xml）
        // 根据配置文件创建HDFS对象
        // 此时必须指定hdsf的访问路径。
        conf = new Configuration();
        // 文件系统为必须设置的内容。其他配置参数可以自行设置，且优先级最高
        conf.set("fs.defaultFS", "hdfs://nameservice1");

        try {
            // 根据配置文件创建HDFS对象
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            e.printStackTrace();

        }

        return fs;
    }

    public static void copyFileToHDFS(InputStream srcInput, String destPath) throws Exception {
        FileSystem fs = getFs();
        OutputStream os = fs.create(new Path(destPath));
        // copy
        IOUtils.copyBytes(srcInput, os, 4096, true);
        System.out.println("拷贝完成...");
        os.flush();
        os.close();
        // fs不要随意关闭
//		fs.close();
    }
}
