package com.wgx.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 15:32:55
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

