package com.wgx.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:30:00
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

