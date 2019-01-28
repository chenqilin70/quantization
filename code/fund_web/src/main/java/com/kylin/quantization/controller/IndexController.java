package com.kylin.quantization.controller;

import com.alibaba.fastjson.JSON;
import com.kylin.quantization.model.Fund;
import com.kylin.quantization.service.IndexService;
import com.kylin.quantization.util.MapUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassName: IndexController
 * Description:
 * Author: aierxuan
 * Date: 2019-01-25 14:16
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@RequestMapping("/index")
@Controller
public class IndexController {
    @Autowired
    private IndexService service;
    @Autowired
    private  MapUtil<String,Object> soMapUtil;

    @RequestMapping("/searchTips")
    public ModelAndView searchTips(String searchWord) {
        List<Fund> result=new ArrayList<>();
        if(StringUtils.isNotBlank(searchWord)){
            result= service.searchTips(searchWord);
        }
        return new ModelAndView("index_searchlist",soMapUtil.create("funds",result));
    }

    @RequestMapping("/index")
    public String getUser() {
        return "index";
    }

    @ResponseBody
    @RequestMapping("/corr_radar")
    public String corrRadar(String fundcode) {
        return JSON.toJSONString(service.corrRadar(fundcode));
    }

}
