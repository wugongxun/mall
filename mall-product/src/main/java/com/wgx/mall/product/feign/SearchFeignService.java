package com.wgx.mall.product.feign;

import com.wgx.common.to.SkuEsTo;
import com.wgx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author wgx
 * @since 2023/3/14 17:44
 */
@FeignClient(name = "mall-search", path = "/search")
public interface SearchFeignService {
    @PostMapping("/save/up")
    R up(@RequestBody List<SkuEsTo> skuEsTos);
}
