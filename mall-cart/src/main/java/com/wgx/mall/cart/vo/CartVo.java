package com.wgx.mall.cart.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author wgx
 * @since 2023/4/1 15:28
 */
@Data
public class CartVo {
    private List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce;

    public Integer getCountNum() {
        return this.items.stream().mapToInt(CartItem::getCount).sum();
    }

    public Integer getCountType() {
        return this.items == null ? 0 : this.items.size();
    }

    public BigDecimal getTotalAmount() {
        if (CollectionUtils.isEmpty(this.items)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : this.items) {
            if (item.getChecked()) {
                sum = sum.add(item.getTotalPrice());
            }
        }
        return sum;
    }
}
