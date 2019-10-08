package com.kylin.listener.service;
import  static com.kylin.listener.util.MapUtil.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kylin.listener.ListenerMain;
import com.kylin.listener.util.ExceptionTool;
import com.kylin.listener.util.HttpUtil;
import com.kylin.listener.util.MailUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.util.*;

/**
 * ClassName: ConvertibleBondChance
 * Description:
 * Author: aierxuan
 * Date: 2019-08-13 21:12
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class ConvertibleBondChance {
    public static Logger logger= LoggerFactory.getLogger(ConvertibleBondChance.class);
    public static void listen() {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Timer timer=new Timer("ConvertibleBondChanceTimer",false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                try{
                    List<DefaultElement> kzzConfig = kzzConfig = ListenerMain.getFileXmlConfig("/root/kzz/*");

                    Set<String> holdSet=new HashSet<>();
                    for (DefaultElement e : kzzConfig) {
                        String code = e.attribute("code").getStringValue();
                        holdSet.add(code.substring(0,code.length()-1));
                    }

                    String s = HttpUtil.doGet(
                            "https://www.jisilu.cn/data/cbnew/cb_list/"
                            , SS.create( )
                    );
                    JSONArray rows = JSONObject.parseObject(s).getJSONArray("rows");
                    Iterator<Object> iterator = rows.iterator();
                    List<JSONObject> result=new ArrayList<>();
                    while(iterator.hasNext()){
                        try{
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
                                item.put("isHold",holdSet.contains(cell.getString("bond_id")));
                                item.put("price",cell.getString("price"));
                                item.put("premium_rt",cell.getString("premium_rt"));
                                result.add(item);
                            }
                        }catch (Exception e){
                            logger.error(ExceptionTool.toString(e));
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
                    StringBuffer sb=new StringBuffer("");
                    for(JSONObject cell:result){
                        sb.append(cell.getString("bond_nm")+"\t"+cell.getString("bond_id")+"\t"+cell.getString("price")+"\t"+cell.getString("premium_rt")+"\t持有："+cell.getBoolean("isHold")+"\n");
                    }
                    MailUtil.send("可转债投资机会",sb.toString());
                }catch (Throwable t){
                    logger.error(ExceptionTool.toString(t));
                    try {
                        MailUtil.send("可转债投资机会","今日投资机会爬取失败，报错如下：\n"+ExceptionTool.toString(t));
                    } catch (MessagingException e) {
                        logger.error(ExceptionTool.toString(e));
                    }
                }
            }
        },calendar.getTime(),24*60*60*1000);
    }
}
