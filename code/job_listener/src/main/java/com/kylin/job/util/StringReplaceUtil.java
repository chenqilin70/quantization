package com.kylin.job.util;

import java.util.Map;

/**
 * Created by aierxuan on 2018-01-08.
 */
public class StringReplaceUtil {
    public static final String PREFIX="\\{",SUFFIX="\\}";
    public static String  replace(String src, Map<String,String> map){
        for(String k:map.keySet()){
            src=src.replaceAll(pack(k),map.get(k));
        }
        return src;
    }
    public static String pack(String src){
        return PREFIX.concat(src).concat(SUFFIX);
    }
}
