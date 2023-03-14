package com.wgx.mall.product.feign;

import com.wgx.common.to.SkuReductionTo;
import com.wgx.common.to.SpuBoundTo;
import com.wgx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wgx
 * @since 2023/3/10 23:27
 */
@FeignClient(name = "mall-coupon", path = "/coupon")
public interface CouponFeignService {

    @PostMapping("/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
