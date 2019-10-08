package com.kylin.listener;
import  static com.kylin.listener.util.MapUtil.*;
import com.alibaba.fastjson.JSONObject;
import com.kylin.listener.service.ConvertibleBondChance;
import com.kylin.listener.service.ConvertibleBondStopProfitListener;
import com.kylin.listener.util.HttpUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * ClassName: ListenerMain
 * Description:
 * Author: aierxuan
 * Date: 2019-08-13 9:42
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class ListenerMain {
    public static void main(String[] args) throws Exception {
        ConvertibleBondStopProfitListener.listen();
        ConvertibleBondChance.listen();


    }

    public static Properties getFileConfig() throws IOException {
        String path = ListenerMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(new File(path).getParentFile(), "config.properties");
        Properties properties=new Properties();
        FileInputStream in = new FileInputStream(file);
        properties.load(in);
        return properties;
    }

    public static List<DefaultElement> getFileXmlConfig(String path) throws Exception {
        String dir = ListenerMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(new File(dir).getParentFile(), "config.xml");
        FileInputStream in = new FileInputStream(file);
        SAXReader reader = new SAXReader();
        Document doc = reader.read(in);
        in.close();
        return doc.selectNodes(path);
    }


}
