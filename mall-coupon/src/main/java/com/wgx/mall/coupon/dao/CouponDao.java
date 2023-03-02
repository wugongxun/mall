package com.wgx.mall.coupon.dao;

import com.wgx.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:30:01
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
