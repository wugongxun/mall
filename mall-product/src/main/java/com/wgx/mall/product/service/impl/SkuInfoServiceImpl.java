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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

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

        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //sku基本信息
            SkuInfoEntity skuInfo = getById(skuId);
            skuItemVo.setSkuInfo(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        CompletableFuture<Void> spuInfoFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //spu基本信息
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(skuInfo.getSpuId());
            skuItemVo.setSpuInfo(spuInfo);
        }, threadPoolExecutor);

        CompletableFuture<Void> groupAttrsFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //spu规格参数
            List<SkuItemVo.SpuItemAttrGroupVo> groupAttrs = attrGroupService.getAttrGroupWithSpuId(skuInfo.getSpuId(), skuInfo.getCatalogId());
            skuItemVo.setGroupAttrs(groupAttrs);
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrsFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //spu的销售属性组合
            List<SkuItemVo.SkuItemSaleAttrsVo> saleAttrs = skuSaleAttrValueService.getSaleAttrsBySpuId(skuInfo.getSpuId());
            skuItemVo.setSaleAttrs(saleAttrs);
        }, threadPoolExecutor);


        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //sku图片信息
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, threadPoolExecutor);

        CompletableFuture.allOf(spuInfoFuture, groupAttrsFuture, saleAttrsFuture, imagesFuture).join();

        //查询是否有货
        skuItemVo.setHasStock(true);

        return skuItemVo;
    }

}