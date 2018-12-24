package com.kylin.quantization.thread;

import com.kylin.quantization.service.CatcherService;

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
public class NetValTask extends BaseRecursiveTask<Map<String,String>,Object>{
    private CatcherService service;
    public NetValTask(List<Map<String, String>> datas, int THRESHOLD_NUM, CatcherService service) {
        super(datas, THRESHOLD_NUM);
        this.service=service;
    }

    @Override
    public Object run(List<Map<String, String>> perIncrementList) {
        perIncrementList.forEach(fund->{
            service.getNetVal(fund);
        });
        return null;
    }

    @Override
    protected Object reduce(Object leftResult, Object rightResult) {
        return null;
    }

    @Override
    public BaseRecursiveTask getBaseRecursiveTask(List<Map<String, String>> dataList, int THRESHOLD_NUM) {
        return new NetValTask(dataList,THRESHOLD_NUM,service);
    }
}