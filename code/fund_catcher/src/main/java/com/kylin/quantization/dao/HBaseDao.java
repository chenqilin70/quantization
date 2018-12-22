package com.kylin.quantization.dao;

/**
 * ClassName: HBaseDao
 * Description:
 * Author: aierxuan
 * Date: 2018-12-21 18:16
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public interface HBaseDao extends BaseDao{
    boolean createTable(String tableName,String ... colums);
}
