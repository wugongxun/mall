package com.wgx.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wgx.common.utils.PageUtils;
import com.wgx.mall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 15:32:55
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

