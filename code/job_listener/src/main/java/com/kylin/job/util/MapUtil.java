package com.kylin.job.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aierxuan on 2018-01-08.
 */
public class MapUtil<K,V> {
    public static final MapUtil<String,String> SS=new MapUtil<>();
    public static final MapUtil<String,Object> SO=new MapUtil<>();
    public static final MapUtil<String,Integer> SI=new MapUtil<>();
    public static final MapUtil<String,Date> SD=new MapUtil<>();
    public Map<K,V> create(Object ... args){
        Map<K,V> map=new HashMap();
        append(map,args);
        return map;
    }

    public void append(Map<K,V> map,Object ...args){
        checkArgs(args);
        put(map,args);
    }
    public Map<K,V> createOrAppend(Map<K,V> map,Object ...args){
        if(map==null){
            map=create(args);
        }else{
            append(map,args);
        }
        return map;
    }
    private void put(Map<K,V> map,Object ...args){
        K key=null;
        V val=null;
        for(int i = 0 ;i<args.length;i++){
            if(i%2==0){
                key= (K) args[i];
            }else{
                val= (V) args[i];
                map.put((K) key,(V) val);
            }
        }
    }
    private void checkArgs(Object ...args){
        if(args.length%2!=0){
            throw new RuntimeException("MapUtil.append参数必须为偶数！");
        }
    }
}
