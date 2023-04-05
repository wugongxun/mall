package com.wgx.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author wgx
 * @since 2023/3/25 17:01
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
public class MallAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallAuthApplication.class, args);
    }
}
