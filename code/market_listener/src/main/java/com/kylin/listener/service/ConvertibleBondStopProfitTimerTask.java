package com.kylin.listener.service;

import com.kylin.listener.ListenerMain;
import com.kylin.listener.util.ExceptionTool;
import com.kylin.listener.util.HttpUtil;
import com.kylin.listener.util.MailUtil;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kylin.listener.util.MapUtil.SS;

/**
 * ClassName: ConvertibleBondStopProfitTimerTask
 * Description:
 * Author: aierxuan
 * Date: 2019-08-13 13:24
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class ConvertibleBondStopProfitTimerTask extends TimerTask {
    public  Set<String> stopProfitSet=new HashSet<>();
    private Timer timer;
    public static final Long period=1000*60l;
    private static final String kzz_price_url="http://pdfm.eastmoney.com/EM_UBG_PDTI_Fast/api/js";
    public static Logger logger= LoggerFactory.getLogger(ConvertibleBondStopProfitTimerTask.class);


    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public ConvertibleBondStopProfitTimerTask(Timer timer) {
        this.timer = timer;
    }

    @Override
    public void run() {
        try {
            List<DefaultElement> kzzConfig = null;
            try {
                kzzConfig = ListenerMain.getFileXmlConfig("/root/kzz/*");
            } catch (Exception e) {
                logger.error(ExceptionTool.toString(e));
            }
            Map<String,Double> priceMap=new HashMap<>();
            for (DefaultElement e : kzzConfig) {
                String code = e.attribute("code").getStringValue();
                String name = e.attribute("name").getStringValue();
                String s = HttpUtil.doGet(kzz_price_url, SS.create("id", code, "style", "top", "num", "1"));
                double price = Double.parseDouble(s.split(",")[1]);
                priceMap.put(name,price);
                if(price>=130 && !stopProfitSet.contains(code)){
                    logger.info("检测到"+name+"转债止盈信号，即将发送邮件通知");
                    stopProfitSet.add(code);
                    MailUtil.send(name+"转债止盈信号",name+"转债已经到达止盈点，当前价格："+price+",请及时止盈。\n-------------少即是多，慢即是快");
                }
            }

            SimpleDateFormat sf=new SimpleDateFormat("HHmm");
            Integer now = Integer.parseInt(sf.format(new Date()));
            Integer end=1500;
            if(now>end){
                Timer newtimer=new Timer("ConvertibleBondStopProfitTimer",false);
                TimerTask newtimerTask = new ConvertibleBondStopProfitTimerTask(newtimer);
                Date firstDate = getFirstDate();
                logger.info("检测到时间超过15点，即将停止循环并发送报告，新任务开启时间："+firstDate.toLocaleString());
                newtimer.schedule(newtimerTask,firstDate,period);
                StringBuffer sb=new StringBuffer("");
                for(String key:priceMap.keySet()){
                    sb.append(key+"\t"+priceMap.get(key)+"\n");
                }
                MailUtil.send("可转债日报",sb+"");
                timer.cancel();
            }
        }catch (Throwable t){
            logger.error(ExceptionTool.toString(t));
        }


    }



    public static Date getFirstDate() {
        SimpleDateFormat sf=new SimpleDateFormat("HHmm");
        Integer now = Integer.parseInt(sf.format(new Date()));
        Integer end=1500;
        Calendar calendar=Calendar.getInstance();
        if(now>=end){
            logger.info("当前时间大于收盘时间，定时器开始日期加一");
            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+1);
        }else{
            logger.info("当前时间小于收盘时间，定时器开始日期为当天");
        }
        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE,30);
        return calendar.getTime();
    }

}
