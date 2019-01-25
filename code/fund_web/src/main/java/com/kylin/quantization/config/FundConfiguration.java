package com.kylin.quantization.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class FundConfiguration  extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/bootstrap/**","/js/**","/css/**","/img/**")
                .addResourceLocations("classpath:/bootstrap/","classpath:/js/","classpath:/css/","classpath:/img/");
        super.addResourceHandlers(registry);
    }
}
