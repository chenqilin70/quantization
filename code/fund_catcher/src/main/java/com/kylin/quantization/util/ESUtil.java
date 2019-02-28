package com.kylin.quantization.util;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetSocketAddress;

public class ESUtil {
    private static PreBuiltTransportClient client;
    /**
     * 获取Es的JAVA客户端
     * @return
     */
    public static Client getEsClient(){
        if(client == null){
            synchronized (ESUtil.class) {
                if(client == null){
                    while(true){
                        try {
                            String esName = "fund_catcher";
                            String[] esIp = "192.168.109.205".split(",");
                            Integer esPort = 9300;
                            // 实例化ES配置信息
                            Settings settings = Settings.builder()
                                    .put("cluster.name", esName)//根据配置文件
                                    .build();
                            // 实例化ES客户端
                            client = new PreBuiltTransportClient(settings);
                            for(int i = 0; i < esIp.length; i ++){
                                client.addTransportAddress(new TransportAddress(new InetSocketAddress(esIp[i], esPort)));
                            }
                            break;
                        } catch (Exception e) {
                            try {
                                System.out.println("getEsClient()异常！！！");
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            continue;
                        }
                    }
                }
            }
        }
        return client;
    }
}
