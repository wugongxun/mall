package com.wgx.mall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wgx
 * @since 2023/4/5 16:25
 */
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            template.header("Cookie", request.getHeader("Cookie"));
        };
    }
}
