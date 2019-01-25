package com.kylin.quantization.service;

import com.kylin.quantization.mapper.FundMapper;
import com.kylin.quantization.model.Fund;
import com.kylin.quantization.model.FundExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: IndexService
 * Description:
 * Author: aierxuan
 * Date: 2019-01-25 15:27
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@Service
public class IndexService extends BaseService {
    @Autowired
    private FundMapper fundMapper;
    public List<Fund> searchTips(String searchWord) {
        return fundMapper.searchTips(searchWord);
    }
}
