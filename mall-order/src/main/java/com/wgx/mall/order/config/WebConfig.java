package com.wgx.mall.order.config;

import com.wgx.mall.order.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wgx
 * @since 2023/4/5 13:58
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/confirm.html").setViewName("confirm");
        registry.addViewController("/detail.html").setViewName("detail");
        registry.addViewController("/list.html").setViewName("list");
        registry.addViewController("/pay.html").setViewName("pay");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserInterceptor()).addPathPatterns("/**");
    }
}
