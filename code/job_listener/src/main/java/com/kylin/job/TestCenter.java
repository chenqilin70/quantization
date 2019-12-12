package com.kylin.job;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * ClassName: TestCenter
 * Description:
 * Author: aierxuan
 * Date: 2019-11-11 9:36
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class TestCenter {
    public static void main(String[] args) throws IOException {
        List<String> lines = FileUtils.readLines(new File("C:\\Users\\aierxuan\\Downloads\\all.txt"));
        for(String line:lines){
            if(StringUtils.isBlank(line)){
                continue;
            }
            while(true){
                String next = new Scanner(System.in).next();
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                if("1".equals(next)){
                    break;
                }
            }


            System.out.println(line);
        }
    }
}
