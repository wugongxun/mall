package com.wgx.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.mall.product.entity.SkuImagesEntity;
import com.wgx.mall.product.entity.SpuInfoDescEntity;
import com.wgx.mall.product.service.*;
import com.wgx.mall.product.vo.SkuItemVo;
import org.bouncycastle.util.test.SimpleTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.SkuInfoDao;
import com.wgx.mall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

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

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        //sku基本信息
        SkuInfoEntity skuInfo = getById(skuId);
        skuItemVo.setSkuInfo(skuInfo);
        Long spuId = skuInfo.getSpuId();
        Long catalogId = skuInfo.getCatalogId();

        //sku图片信息
        List<SkuImagesEntity> images =  skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(images);

        //spu基本信息
        SpuInfoDescEntity spuInfo = spuInfoDescService.getById(spuId);
        skuItemVo.setSpuInfo(spuInfo);

        //spu规格参数
        List<SkuItemVo.SpuItemAttrGroupVo> groupAttrs = attrGroupService.getAttrGroupWithSpuId(spuId, catalogId);
        skuItemVo.setGroupAttrs(groupAttrs);

        //spu的销售属性组合
        List<SkuItemVo.SkuItemSaleAttrsVo> saleAttrs =  skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVo.setSaleAttrs(saleAttrs);

        //查询是否有货
        skuItemVo.setHasStock(true);

        return skuItemVo;
    }

}