package com.kylin.quantization.component;

import com.kylin.quantization.service.CatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: CorrIndexRunner
 * Description:
 * Author: aierxuan
 * Date: 2019-01-30 15:21
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Component
public class CorrIndexRunner  extends CatcherRunner {
    @Autowired
    private CatcherService service;
    @Override
    protected String getTask() {
        return "corrIndex";
    }

    @Override
    protected void doTask() {
        service.corrIndex();
    }
}






















