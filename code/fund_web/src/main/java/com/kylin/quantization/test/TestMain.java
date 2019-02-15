package com.kylin.quantization.test;

import com.kylin.quantization.util.JedisUtil;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ClassName: TestMain
 * Description:
 * Author: aierxuan
 * Date: 2019-02-15 16:28
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class TestMain {

    public static void main(String[] args) {
        String k1 = JedisUtil.jedis(jedis -> jedis.get("k1"));
        System.out.println(k1);
    }
}
