package com.wgx.mall.product.feign;

import com.wgx.common.utils.R;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author wgx
 * @since 2023/3/3 15:48
 */
@FeignClient(value = "mall-order", path = "/order/order")
public interface OrderFeign {
    @LoadBalanced
    @RequestMapping("/list")
    R list(Map<String, Object> params);
}
