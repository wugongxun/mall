package com.wgx.mall.ware.feign;

import com.wgx.common.utils.R;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wgx
 * @since 2023/3/11 17:48
 */
@FeignClient(name = "mall-product", path = "/product")
public interface ProductFeignService {

    @LoadBalanced
    @RequestMapping("/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

}
