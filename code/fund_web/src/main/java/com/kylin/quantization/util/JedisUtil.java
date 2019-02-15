package com.kylin.quantization.util;

import com.kylin.quantization.test.TestMain;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ClassName: JedisUtil
 * Description:
 * Author: aierxuan
 * Date: 2019-02-15 17:26
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class JedisUtil {
    public static JedisPool pool=null;

    /**
     * 静态代码块只执行一次
     */
    static{
        //加载配置文件
        InputStream in = TestMain.class.getClassLoader().getResourceAsStream("redis.properties");
        //创建配置文件对象
        Properties prop = new Properties();
        try {
            //加载文件流
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
//            IOUtils.closeQuietly(in);
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(Integer.parseInt(prop.get("redis.MaxTotal").toString()));//最大连接数，连接全部用完，进行等待
        poolConfig.setMinIdle(Integer.parseInt(prop.get("redis.MinIdle").toString())); //最小空余数
        poolConfig.setMaxIdle(Integer.parseInt(prop.get("redis.MaxIdle").toString())); //最大空余数
        poolConfig.setMaxWaitMillis(Long.parseLong(prop.get("redis.MaxWaitMillis").toString()));
        pool = new JedisPool(poolConfig,prop.get("redis.host").toString()
                ,Integer.parseInt(prop.get("redis.port").toString())
                ,5000,"111111");
    }

    /**
     * 对外提供静态方法
     * @return
     */
    public static Jedis get() {
        return pool.getResource();
    }
    public static <T> T jedis(JedisRunner<T> runner){
        Jedis jedis = get();
        T t= runner.run(jedis);
        jedis.close();
        return t;

    }
    public  static interface JedisRunner<T>{
        public  T run(Jedis jedis);
    }
}
