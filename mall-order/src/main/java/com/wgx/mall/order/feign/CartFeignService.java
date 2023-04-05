package com.wgx.mall.order.feign;

import com.wgx.mall.order.vo.CartItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author wgx
 * @since 2023/4/5 15:33
 */
@FeignClient(name = "mall-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItems")
    List<CartItemVo> currentUserCartItems();
}
