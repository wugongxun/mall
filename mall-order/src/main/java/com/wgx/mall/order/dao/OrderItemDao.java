package com.wgx.mall.order.dao;

import com.wgx.mall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:39:57
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
