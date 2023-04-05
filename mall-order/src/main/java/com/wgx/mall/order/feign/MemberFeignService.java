package com.wgx.mall.order.feign;

import com.wgx.mall.order.vo.CartItemVo;
import com.wgx.mall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author wgx
 * @since 2023/4/5 15:19
 */
@FeignClient(name = "mall-member", path = "/member")
public interface MemberFeignService {

    @GetMapping("/memberreceiveaddress/{memberId}/addresses")
    List<MemberAddressVo> getAddresses(@PathVariable("memberId") Long memberId);

}
