package com.kylin.quantization.util;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetSocketAddress;

public class ESUtil {
    //集群名,默认值elasticsearch
    private static final String CLUSTER_NAME = "fund_catcher";
    //ES集群中某个节点
    private static final String HOSTNAME = "192.168.109.205";
    //连接端口号
    private static final int TCP_PORT = 9300;
    //构建Settings对象
    private static Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
    //TransportClient对象，用于连接ES集群
    private static volatile TransportClient client;
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
                            String[] esIp = HOSTNAME.split(",");
                            // 实例化ES客户端
                            client = new PreBuiltTransportClient(settings);
                            for(int i = 0; i < esIp.length; i ++){
                                client.addTransportAddress(new TransportAddress(new InetSocketAddress(esIp[i], TCP_PORT)));
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


    /**
     * 获取索引管理的IndicesAdminClient
     */
    public static IndicesAdminClient getAdminClient() {
        return getEsClient().admin().indices();
    }

    /**
     * 判定索引是否存在
     * @param indexName
     * @return
     */
    public static boolean isExists(String indexName){
        IndicesExistsResponse response=getAdminClient().prepareExists(indexName).get();
        return response.isExists()?true:false;
    }
    /**
     * 创建索引
     * @param indexName
     * @return
     */
    public static boolean createIndex(String indexName){
        CreateIndexResponse createIndexResponse = getAdminClient()
                .prepareCreate(indexName.toLowerCase())
                .get();
        return createIndexResponse.isAcknowledged()?true:false;
    }

    /**
     * 创建索引
     * @param indexName 索引名
     * @param shards   分片数
     * @param replicas  副本数
     * @return
     */
    public static boolean createIndex(String indexName, int shards, int replicas) {
        Settings createIndexSettings = Settings.builder()
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)
                .build();
        CreateIndexResponse createIndexResponse = getAdminClient()
                .prepareCreate(indexName.toLowerCase())
                .setSettings(createIndexSettings)
                .execute().actionGet();
        return createIndexResponse.isAcknowledged()?true:false;
    }

    /**
     * 位索引indexName设置mapping
     * @param indexName
     * @param typeName
     * @param mapping
     */
    public static void setMapping(String indexName, String typeName, String mapping) {
        getAdminClient().preparePutMapping(indexName)
                .setType(typeName)
                .setSource(mapping, XContentType.JSON)
                .get();
    }

    /**
     * 删除索引
     * @param indexName
     * @return
     */
    public static boolean deleteIndex(String indexName) {
        AcknowledgedResponse deleteResponse = getAdminClient()
                .prepareDelete(indexName.toLowerCase())
                .execute()
                .actionGet();
        return deleteResponse.isAcknowledged()?true:false;
    }



}
