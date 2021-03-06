package com.kylin.quantization.thread;

import com.kylin.quantization.service.CatcherService;
import com.kylin.quantization.util.ExceptionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * ClassName: FundBaseTask
 * Description:
 * Author: aierxuan
 * Date: 2018-12-24 14:48
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class StockNoticeTask extends BaseRecursiveTask<String,Object>{
    private CatcherService service;
    public static Logger logger= LoggerFactory.getLogger(StockNoticeTask.class);
    public StockNoticeTask(List<String> datas, int THRESHOLD_NUM, CatcherService service) {
        super(datas, THRESHOLD_NUM);
        this.service=service;
    }

    @Override
    public Object run(List<String> perIncrementList) {
        perIncrementList.forEach(item->{
            try {
                service.stockNotice(item);
            }catch (Exception e){
                logger.info(ExceptionTool.toString(e));
            }
        });
        return null;
    }

    @Override
    protected Object reduce(Object leftResult, Object rightResult) {
        return null;
    }

    @Override
    public BaseRecursiveTask getBaseRecursiveTask(List<String> dataList, int THRESHOLD_NUM) {
        return new StockNoticeTask(dataList,THRESHOLD_NUM,service);
    }
}
