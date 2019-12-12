package com.kylin.job;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.job.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.*;

import  static com.kylin.job.util.MapUtil.*;

/**
 * ClassName: JobCatcher
 * Description:
 * Author: aierxuan
 * Date: 2019-11-05 16:27
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class JobCatcher {
    public static void main(String[] args) throws IOException {
        String url="https://fe-api.zhaopin.com/c/i/sou";
        Set<String> addr=new HashSet<String>();
        Map<String , Integer> addrMap=new HashMap<>();
        Map<String , Integer> salaryMap=new HashMap<>();
        for(int i=1;i<10;i++){
            Map<String, String> params = SS.create(
                    "start",((i-1)*90)+""
                    , "pageSize", "90"
                    , "cityId", "736"//736 武汉
                    , "workExperience", "-1"
                    , "education", "-1"
                    , "companyType", "-1"
                    , "employmentType", "-1"
                    , "jobWelfareTag", "-1"
                    , "kw", "大数据开发"
                    , "kt", "3"
                    , "_v", "0.89463576"
                    , "x-zp-page-request-id", "a3fab77803e44ba7bb1baa5542ed2197-1572942580381-652445"
                    , "x-zp-client-id", "336dc4c5-605d-44d1-b574-304c5d821441");
            String s = HttpUtil.doGet(url, params);
            JSONArray results = JSON.parseObject(s).getJSONObject("data").getJSONArray("results");
            System.out.println("获取数量："+results.size());
            Iterator<Object> iterator = results.iterator();
            while(iterator.hasNext()){
                JSONObject next = (JSONObject) iterator.next();
                String businessArea = next.getString("businessArea");
                String salary = next.getString("salary");
                Integer integer = addrMap.get(businessArea);
                if(integer==null){
                    integer=0;
                }
                integer++;
                addrMap.put(businessArea,integer);


                Integer salaryinteger = addrMap.get(businessArea);
                if(salaryinteger==null){
                    salaryinteger=0;
                }
                salaryinteger++;
                salaryMap.put(salary,salaryinteger);

            }


        }

        for(String key:addrMap.keySet()){
            System.out.println(key+"    "+addrMap.get(key));
        }
        System.out.println("================");
        for(String key:salaryMap.keySet()){
            System.out.println(key+"    "+salaryMap.get(key));
        }



    }

}
