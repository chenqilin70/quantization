package com.kylin.quantization.service;

import com.kylin.quantization.mapper.FundMapper;
import com.kylin.quantization.mapper.IndexFundCorrMapper;
import com.kylin.quantization.model.Fund;
import com.kylin.quantization.model.IndexFundCorr;
import com.kylin.quantization.model.IndexFundCorrExample;
import com.kylin.quantization.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private IndexFundCorrMapper indexFundCorrMapper;
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    public List<Fund> searchTips(String searchWord) {
        return fundMapper.searchTips(searchWord);
    }

    public Map<String,List<String>>  corrRadar(String fundcode) {
        MapUtil<String,BigDecimal> sbMapUtil=new MapUtil<>();
        IndexFundCorrExample indexFundCorrExample = new IndexFundCorrExample();
        indexFundCorrExample.createCriteria().andFundcodeEqualTo(fundcode);
        List<IndexFundCorr> indexFundCorrs = indexFundCorrMapper.selectByExample(indexFundCorrExample);



        Map<String,List<String>> result=new HashMap<>();
        if(indexFundCorrs!=null && indexFundCorrs.size()!=0){
            Map<Integer,List<IndexFundCorr>> corrTypeMap=new HashMap<>();
            indexFundCorrs.forEach(i->{
                List<IndexFundCorr> values = corrTypeMap.get(i.getCorrtype());
                if(values==null){
                    values=new ArrayList<>();
                }
                values.add(i);
                corrTypeMap.put(i.getCorrtype(),values);
            });
            corrTypeMap.forEach((t,cs)->{
                List<String> corrs=new ArrayList<>();
                Map<String, BigDecimal> stringStringMap = cs.stream().map(i -> sbMapUtil.create(i.getIndexcode() + "", i.getCorrelationindex() )).reduce((m1, m2) -> {
                    m1.putAll(m2);
                    return m1;
                }).get();
                String indexsStr="SH000001,SZ399001,SH000300,SZ399006,SH000905,SZ399330,SH000016";
                String[] split = indexsStr.split(",");

                for(String s: split){
                    BigDecimal s1 = stringStringMap.get(s);
                    corrs.add(s1+"");
                }
                result.put(t+"",corrs);
            });

        }
        return result;

    }
}
