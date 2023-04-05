package com.wgx.mall.cart.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.wgx.common.to.SkuInfoTo;
import com.wgx.common.to.UserInfoTo;
import com.wgx.common.utils.R;
import com.wgx.mall.cart.feign.ProductFeign;
import com.wgx.mall.cart.interceptor.CartInterceptor;
import com.wgx.mall.cart.service.CartService;
import com.wgx.mall.cart.vo.CartItem;
import com.wgx.mall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author wgx
 * @since 2023/4/1 15:50
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeign productFeign;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    private final String CART_PREFIX = "cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();

        String s = (String) ops.get(skuId.toString());
        if (StringUtils.hasLength(s)) {
            CartItem cartItem = JSON.parseObject(s, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);

            CompletableFuture.runAsync(() -> ops.put(skuId.toString(), JSON.toJSONString(cartItem)), threadPoolExecutor);

            return cartItem;
        }

        CartItem cartItem = new CartItem();
        CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
            R<SkuInfoTo> r = productFeign.info(skuId);
            SkuInfoTo skuInfo = r.getData(new TypeReference<SkuInfoTo>() {
            });
            cartItem.setSkuId(skuId);
            cartItem.setCount(num);
            cartItem.setChecked(true);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setPrice(skuInfo.getPrice());
            cartItem.setTitle(skuInfo.getSkuTitle());

        }, threadPoolExecutor);

        CompletableFuture<Void> skuAttrTask = CompletableFuture.runAsync(() -> cartItem.setSkuAttr(productFeign.stringList(skuId)), threadPoolExecutor);

        skuInfoTask.runAfterBothAsync(skuAttrTask, () -> ops.put(skuId.toString(), JSON.toJSONString(cartItem)), threadPoolExecutor);

        CompletableFuture.allOf(skuInfoTask, skuAttrTask).join();

        return cartItem;
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        String s = (String) ops.get(skuId.toString());
        return JSON.parseObject(s, CartItem.class);
    }

    @Override
    public CartVo getCart() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        CartVo cart = new CartVo();

        if (userInfoTo.getUserId() == null) {
            //未登录状态，直接取出临时购物车的数据
            cart.setItems(getCartItemsByRedisKey(CART_PREFIX + userInfoTo.getUserKey()));
        } else {
            //已登录状态，如果临时购物车中也有数据，需要合并
            CopyOnWriteArrayList<CartItem> cartItems = new CopyOnWriteArrayList<>(getCartItemsByRedisKey(CART_PREFIX + userInfoTo.getUserId()));

            List<CartItem> tempCartItems = getCartItemsByRedisKey(CART_PREFIX + userInfoTo.getUserKey());
            if (!CollectionUtils.isEmpty(tempCartItems)) {
                tempCartItems.forEach(tempCartItem -> {
                    boolean flag = false;
                    for (CartItem cartItem : cartItems) {
                        if (cartItem.getSkuId().equals(tempCartItem.getSkuId())) {
                            cartItem.setCount(cartItem.getCount() + tempCartItem.getCount());
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        cartItems.add(tempCartItem);
                    }
                });


                CompletableFuture.runAsync(() -> {
                    redisTemplate.delete(CART_PREFIX + userInfoTo.getUserKey());
                    redisTemplate.delete(CART_PREFIX + userInfoTo.getUserId());
                    BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(CART_PREFIX + userInfoTo.getUserId());
                    cartItems.forEach(cartItem -> ops.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem)));
                });
            }

            cart.setItems(cartItems);

        }
        return cart;

    }

    @Override
    public void deleteCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        ops.delete(skuId.toString());
    }

    @Override
    public List<CartItem> currentUserCartItems() {
        List<CartItem> res = new ArrayList<>();
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        List<Object> values = ops.values();
        if (!CollectionUtils.isEmpty(values)) {
            values.forEach(item -> {
                CartItem cartItem = JSON.parseObject((String) item, CartItem.class);
                if (cartItem.getChecked()) {
                    res.add(cartItem);
                }
            });
        }
        return res;
    }

    public BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey;
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        return redisTemplate.boundHashOps(cartKey);
    }

    public List<CartItem> getCartItemsByRedisKey(String key) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);
        List<Object> values = ops.values();
        if (!CollectionUtils.isEmpty(values)) {
            return values.stream().map(item -> JSON.parseObject((String) item, CartItem.class)).collect(Collectors.toList());
        }
        return null;
    }
}
