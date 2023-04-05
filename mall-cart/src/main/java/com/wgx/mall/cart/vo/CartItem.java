package com.wgx.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wgx
 * @since 2023/4/1 15:29
 */
@Data
public class CartItem {
    private Long skuId;
    private Boolean checked;
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
