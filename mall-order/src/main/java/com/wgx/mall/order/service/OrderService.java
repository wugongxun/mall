package com.wgx.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:39:57
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

