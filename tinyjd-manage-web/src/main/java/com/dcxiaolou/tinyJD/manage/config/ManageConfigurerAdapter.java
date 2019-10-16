package com.dcxiaolou.tinyJD.manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ManageConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Bean
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
        WebMvcConfigurerAdapter adapter = new WebMvcConfigurerAdapter() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/manage").setViewName("param-list");
                registry.addViewController("/param-list.html").setViewName("param-list");
                registry.addViewController("/toAttrInfoPage").setViewName("param-add");
                registry.addViewController("/index").setViewName("index");
                registry.addViewController("/index.html").setViewName("index");
                registry.addViewController("/spu-manage.html").setViewName("spu-manage");
            }
        };
        return adapter;
    }

}
