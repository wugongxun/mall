package com.wgx.mall.order.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wgx
 * @since 2023/4/5 15:05
 */
@Data
public class OrderConfirmVo {
    private List<MemberAddressVo> memberAddressVos;
    private List<CartItemVo> cartItemVos;
    private Integer integration;
    private BigDecimal total;
    private BigDecimal payPrice;

    public BigDecimal getTotal() {
        if (!CollectionUtils.isEmpty(cartItemVos)) {
            return cartItemVos.stream().map(CartItemVo::getTotalPrice).reduce((sum, item) -> sum.add(item)).get();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
