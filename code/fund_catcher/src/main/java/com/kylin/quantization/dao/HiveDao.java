package com.kylin.quantization.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * ClassName: HiveDao
 * Description:
 * Author: aierxuan
 * Date: 2019-01-30 11:15
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public interface HiveDao extends BaseDao {
    Connection getConn();
    List<Map<String,Object>> executeSql(String tab,boolean result);
    List<Map<String, Object>> executeSql(String tab,boolean result,Map<String,String> params);

}
