package com.kylin.quantization.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ClassName: ForkJoinExecutor
 * Description:
 * Author: aierxuan
 * Date: 2018-12-28 17:28
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class ForkJoinExecutor {

    public  static <R> Object exec(RecursiveTask<R> task,Integer parallelism){
        ForkJoinPool baseInfoPool=new ForkJoinPool(parallelism);
        R r=baseInfoPool.invoke(task);
        baseInfoPool.shutdown();
        return r;
    }
}
