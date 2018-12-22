package com.kylin.quantization.dao.impl;

import com.kylin.quantization.dao.BaseDao;
import com.kylin.quantization.dao.HBaseDao;
import org.springframework.stereotype.Repository;

/**
 * ClassName: HBaseDaoImpl
 * Description:
 * Author: aierxuan
 * Date: 2018-12-21 18:16
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Repository
public class HBaseDaoImpl extends BaseDaoImpl implements HBaseDao{


    public boolean createTable(String tableName,String ... colums) {
        return false;
    }

    @Override
    public boolean dropTable(String tableName) {
        return false;
    }
}
