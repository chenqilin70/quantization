package com.kylin.quantization.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kylin.quantization.mapper.CorrIndexMapper;
import com.kylin.quantization.mapper.FundMapper;
import com.kylin.quantization.mapper.IndexFundCorrMapper;
import com.kylin.quantization.model.*;
import com.kylin.quantization.util.MapUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private CorrIndexMapper corrIndexMapper;
    @Autowired
    private MapUtil<String,String> ssMapUtil;
    @Value("${indexs}")
    private String indexs;
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

    public Map<String,Object> getCorrIndex() {
        Map<String,String> indexI18n=getIndexI18n();
        Map<String,Object> result=new HashMap<>();
        List<CorrIndex> corrIndices = corrIndexMapper.selectByExample(new CorrIndexExample());
        corrIndices.forEach(c->{
            c.setIndex1(indexI18n.get(c.getIndex1()));
            c.setIndex2(indexI18n.get(c.getIndex2()));
        });
        List<String> collect = corrIndices.stream().flatMap(corrIndex -> Sets.newHashSet(corrIndex.getIndex1(), corrIndex.getIndex2()).stream()).distinct().collect(Collectors.toList());
        Collections.sort(collect);
        result.put("index1",collect);
        result.put("index2",collect);
        List<List<? extends Number>> data=new ArrayList<>();
        for(int i1=0;i1<collect.size(); i1++){
            for(int i2=0;i2<collect.size(); i2++){
                BigDecimal corr=getCorrBy2Index(collect.get(i1),collect.get(i2),corrIndices);
                List<? extends Number> numbers = Lists.newArrayList(i1, i2, corr);
                data.add(numbers);
            }
        }
        result.put("data",data);
        return result;
    }

    private BigDecimal getCorrBy2Index(String s, String s1, List<CorrIndex> corrIndices) {
        BigDecimal result;
        if(StringUtils.isBlank(s) || StringUtils.isBlank(s1)){
            result= null;
        }else{
            if(s.equals(s1)){
                result= new BigDecimal("1");
            }else{
                List<CorrIndex> collect = corrIndices.stream().filter(c -> (s.equals(c.getIndex1()) && s1.equals(c.getIndex2())) || (s.equals(c.getIndex2()) && s1.equals(c.getIndex1()))).collect(Collectors.toList());
                result=collect.get(0).getCorrratio();
            }
        }
        return result;


    }

    public Map<String,String> getIndexI18n() {
        Map<String,String> result= Maps.newHashMap();
        String[] split = indexs.split(",");
        for(String s:split){
            String[] split1 = s.split("/");
            result.put(split1[0],split1[1]);
        }
        return result;
    }
}
