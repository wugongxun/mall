package com.wgx.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:39:57
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

