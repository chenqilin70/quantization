package com.kylin.quantization.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.quantization.dao.HBaseDao;
import com.kylin.quantization.util.*;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: ConvertibleBondRunner
 * Description:
 * Author: aierxuan
 * Date: 2019-02-16 12:02
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Component
public class ConvertibleBondRunner   extends CatcherRunner {
    @Autowired
    private HBaseDao hBaseDao;
    @Autowired
    private Map<String,String> conf;
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Override
    protected String getTask() {
        return "convertibleBond";
    }

    @Override
    protected void doTask() {
        List<String> wds = Arrays.asList("kzz_lb,kzz_mx,kzz_fxlx,kzz_zqh,kzz_czyt,kzz_zyrq,kzz_hssh".split(","));
        wds.forEach(w->{
            String tableName = w.replaceAll("_", "");
            hBaseDao.createTableIfNotExist(tableName,"baseinfo");
            hBaseDao.admin(admin -> {admin.truncateTable(TableName.valueOf(tableName),false);return null;});
        });
        String result = HttpUtil.doGet(conf.get("convertiblebond_list"), null);
        JSONArray bonds = JSON.parseObject(result).getJSONArray("data");
        bonds.stream().map(b->((JSONObject)b).getString("BONDCODE")).forEach(code->{
            System.out.println(code);
            wds.forEach(w->{
                String url= StringReplaceUtil.replace(conf.get(w),ssMapUtil.create("bondCode",code.trim()));
                String tableName = w.replaceAll("_", "");
                String dataStr = HttpUtil.doGet(url, null);
                JSONArray dataArr = JSON.parseArray(dataStr);
                int index=0;
                List<Put> putList=new ArrayList<>();
                dataArr.forEach(dataObj->{
                    JSONObject datajson=((JSONObject)dataObj);
                    datajson.keySet().forEach(key->{
                        Put put = PutUtil.getPut(RowKeyUtil.getConvertibleBond(code, index), "baseinfo", key, datajson.getString(key));
                        putList.add(put);
                    });
                });
                hBaseDao.putData(tableName,putList);
            });

        });



    }
}
