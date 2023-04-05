package com.wgx.mall.order.service.impl;

import com.wgx.common.to.MemberTo;
import com.wgx.mall.order.feign.CartFeignService;
import com.wgx.mall.order.feign.MemberFeignService;
import com.wgx.mall.order.interceptor.LoginUserInterceptor;
import com.wgx.mall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.order.dao.OrderDao;
import com.wgx.mall.order.entity.OrderEntity;
import com.wgx.mall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private ThreadPoolExecutor executors;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberTo loginUser = LoginUserInterceptor.threadLocal.get();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> addressesTask = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            orderConfirmVo.setMemberAddressVos(memberFeignService.getAddresses(loginUser.getId()));
        }, executors);

        CompletableFuture<Void> cartItemsTask = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            orderConfirmVo.setCartItemVos(cartFeignService.currentUserCartItems());
        }, executors);

        orderConfirmVo.setIntegration(loginUser.getIntegration());

        CompletableFuture.allOf(addressesTask, cartItemsTask).join();

        System.out.println(orderConfirmVo);

        return orderConfirmVo;
    }

}