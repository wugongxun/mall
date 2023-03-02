package com.wgx.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 16:30:01
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

