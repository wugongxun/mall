package com.wgx.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wgx
 * @since 2023/4/5 15:14
 */
@Data
public class CartItemVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(this.count));
    }
}
