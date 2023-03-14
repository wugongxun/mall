package com.wgx.mall.product.feign;

import com.wgx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author wgx
 * @since 2023/3/14 17:02
 */
@FeignClient(name = "mall-ware", path = "/ware")
public interface WareFeignService {
    @PostMapping("/waresku/hasStock")
    R<Map<Long, Boolean>> hasStock(@RequestBody List<Long> skuIds);
}
