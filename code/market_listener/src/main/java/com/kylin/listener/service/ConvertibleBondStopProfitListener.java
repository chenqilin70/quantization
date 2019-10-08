package com.kylin.listener.service;

import com.kylin.listener.ListenerMain;
import com.kylin.listener.test.TestCenter;
import com.kylin.listener.util.HttpUtil;
import org.dom4j.Attribute;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kylin.listener.util.MapUtil.SS;

/**
 * ClassName: ConvertibleBondStopProfitListener
 * Description:
 * Author: aierxuan
 * Date: 2019-08-13 9:44
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class ConvertibleBondStopProfitListener {
    public static Logger logger= LoggerFactory.getLogger(TestCenter.class);
    public static void listen() throws Exception {
        logger.info("开始启动可转债止损监听器。。。");
        final Timer timer=new Timer("ConvertibleBondStopProfitTimer",false);
        TimerTask timerTask = new ConvertibleBondStopProfitTimerTask(timer);
        Date firstDate = ConvertibleBondStopProfitTimerTask.getFirstDate();
        System.out.println("任务开始时间："+firstDate.toLocaleString());
        timer.schedule(timerTask,firstDate,ConvertibleBondStopProfitTimerTask.period);

    }


}
