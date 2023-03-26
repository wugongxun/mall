package com.wgx.mall.auth.feign;

import com.wgx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wgx
 * @since 2023/3/26 17:02
 */
@FeignClient(name = "mall-other", path = "other")
public interface OtherService {
    @GetMapping("/message/send")
    R send(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
