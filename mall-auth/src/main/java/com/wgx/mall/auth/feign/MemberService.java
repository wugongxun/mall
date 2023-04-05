package com.wgx.mall.auth.feign;

import com.wgx.common.to.MemberTo;
import com.wgx.common.to.SocialUserTo;
import com.wgx.common.utils.R;
import com.wgx.mall.auth.vo.UserLoginVo;
import com.wgx.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wgx
 * @since 2023/3/27 17:03
 */
@FeignClient(name = "mall-member", path = "/member")
public interface MemberService {
    @PostMapping("/member/register")
    R register(@RequestBody UserRegisterVo registerVo);

    @PostMapping("/member/login")
    R<MemberTo> login(@RequestBody UserLoginVo loginVo);

    @PostMapping("/member/socialLogin")
    R<MemberTo> socialLogin(@RequestBody SocialUserTo socialUserTo);
}
