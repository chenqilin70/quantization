package com.kylin.listener.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.listener.util.HttpUtil;
import com.kylin.listener.util.MailUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kylin.listener.util.MapUtil.SS;

/**
 * ClassName: TestCenter
 * Description:
 * Author: aierxuan
 * Date: 2019-08-13 12:59
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class TestCenter {
    public static Logger logger= LoggerFactory.getLogger(TestCenter.class);
    public static void main(String[] args) throws ParseException, MessagingException {

        String s = HttpUtil.doGet(
                "https://www.jisilu.cn/data/cbnew/cb_list/?___jsl=LST___t=1565705582720"
                , SS.create( )
        );
        JSONArray rows = JSONObject.parseObject(s).getJSONArray("rows");
        Iterator<Object> iterator = rows.iterator();
        List<JSONObject> result=new ArrayList<>();
        while(iterator.hasNext()){
            JSONObject row = (JSONObject) iterator.next();
            JSONObject cell = row.getJSONObject("cell");
            JSONObject item=new JSONObject();
            if(StringUtils.isNotBlank(cell.getString("premium_rt"))
                    && Double.parseDouble(StringUtils.removeEnd(cell.getString("premium_rt"),"%")) < 30
                    && StringUtils.isNotBlank(cell.getString("price"))
                    && Double.parseDouble(cell.getString("price"))<105
                    && !"待上市".equals(cell.getString("price_tips"))){
                item.put("bond_nm",cell.getString("bond_nm"));
                item.put("bond_id",cell.getString("bond_id"));
                item.put("price",cell.getString("price"));
                item.put("premium_rt",cell.getString("premium_rt"));
                result.add(item);
//                logger.info(cell.getString("bond_nm")+"\t"+cell.getString("bond_id")+"\t"+cell.getString("price")+"\t"+cell.getString("premium_rt"));
            }
        }

        Collections.sort(result, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                double tmp = Double.parseDouble(o1.getString("price"))-Double.parseDouble(o2.getString("price"));
                int i=tmp<0?-1:(tmp==0?0:1);
                if(i==0){
                    tmp=Double.parseDouble(StringUtils.removeEnd(o1.getString("premium_rt"),"%"))-(Double.parseDouble(StringUtils.removeEnd(o2.getString("premium_rt"),"%")));
                    i=tmp<0?-1:(tmp==0?0:1);
                }
                return i;
            }
        });
        for(JSONObject cell:result){
            logger.info(cell.getString("bond_nm")+"\t"+cell.getString("bond_id")+"\t"+cell.getString("price")+"\t"+cell.getString("premium_rt"));
        }




    }
}
