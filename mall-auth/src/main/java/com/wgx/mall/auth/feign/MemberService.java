package com.wgx.mall.auth.feign;

import com.wgx.common.utils.R;
import com.wgx.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author wgx
 * @since 2023/3/27 17:03
 */
@FeignClient(name = "mall-member", path = "/member")
public interface MemberService {
    @PostMapping("/member/register")
    R register(UserRegisterVo registerVo);
}
