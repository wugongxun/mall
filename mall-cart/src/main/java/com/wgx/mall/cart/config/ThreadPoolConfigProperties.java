package com.wgx.mall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wgx
 * @since 2023/3/25 16:39
 */
@Component
@ConfigurationProperties(prefix = "thread")
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize = 10;
    private Integer maxSize = 100;
    private Integer keepAliveTime = 100;
}
