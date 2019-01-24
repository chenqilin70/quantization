package com.kylin.quantization.config;

import com.kylin.quantization.FundWebMain;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ClassName: ServletInitializer
 * Description:
 * Author: aierxuan
 * Date: 2019-01-24 18:18
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FundWebMain.class);
    }

}