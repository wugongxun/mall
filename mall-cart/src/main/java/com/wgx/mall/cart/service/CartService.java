package com.wgx.mall.cart.service;

import com.wgx.mall.cart.vo.CartItem;
import com.wgx.mall.cart.vo.CartVo;

import java.util.List;

/**
 * @author wgx
 * @since 2023/4/1 15:50
 */
public interface CartService {
    CartItem addToCart(Long skuId, Integer num);

    CartItem getCartItem(Long skuId);

    CartVo getCart();

    void deleteCartItem(Long skuId);

    List<CartItem> currentUserCartItems();
}
