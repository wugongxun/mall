package com.wgx.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.SkuInfoDao;
import com.wgx.mall.product.entity.SkuInfoEntity;
import com.wgx.mall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        Integer min = Integer.valueOf((String) params.get("min"));
        Integer max = Integer.valueOf((String) params.get("max"));

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                Wrappers.lambdaQuery(SkuInfoEntity.class)
                        .eq(StringUtils.hasLength(catelogId) && !"0".equals(catelogId), SkuInfoEntity::getCatalogId, catelogId)
                        .eq(StringUtils.hasLength(brandId) && !"0".equals(brandId), SkuInfoEntity::getBrandId, brandId)
                        .and(StringUtils.hasLength(key), w -> {
                            w.eq(SkuInfoEntity::getSkuId, key)
                                    .or()
                                    .like(SkuInfoEntity::getSkuName, key);
                        })
                        .ge(min != null && min != 0, SkuInfoEntity::getPrice, min)
                        .le(max != null && max != 0, SkuInfoEntity::getPrice, max)

        );

        return new PageUtils(page);
    }

}