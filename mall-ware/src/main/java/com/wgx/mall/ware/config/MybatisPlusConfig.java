package com.wgx.mall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wgx
 * @since 2023/3/11 17:35
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = "com.wgx.mall.ware.dao")
public class MybatisPlusConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setOverflow(false);
        paginationInterceptor.setLimit(1000l);
        return paginationInterceptor;
    }
}
