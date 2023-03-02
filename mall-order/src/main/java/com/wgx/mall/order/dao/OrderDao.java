package com.wgx.mall.order.dao;

import com.wgx.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:39:57
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
