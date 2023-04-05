package com.wgx.mall.cart.feign;

import com.wgx.common.to.SkuInfoTo;
import com.wgx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author wgx
 * @since 2023/4/1 17:12
 */
@FeignClient(name = "mall-product", path = "/product")
public interface ProductFeign {

    @RequestMapping("/skuinfo/info/{skuId}")
    R<SkuInfoTo> info(@PathVariable("skuId") Long skuId);

    @GetMapping("/skusaleattrvalue/stringList/{skuId}")
    List<String> stringList(@PathVariable("skuId") Long skuId);

}
