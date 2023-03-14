package com.wgx.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.constants.ProductConstant;
import com.wgx.common.to.SkuEsTo;
import com.wgx.common.to.SkuReductionTo;
import com.wgx.common.to.SpuBoundTo;
import com.wgx.common.utils.R;
import com.wgx.mall.product.dao.SpuInfoDescDao;
import com.wgx.mall.product.entity.*;
import com.wgx.mall.product.feign.CouponFeignService;
import com.wgx.mall.product.feign.SearchFeignService;
import com.wgx.mall.product.feign.WareFeignService;
import com.wgx.mall.product.service.*;
import com.wgx.mall.product.vo.*;
import jdk.nashorn.internal.runtime.options.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    //TODO 分布式事务
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //保存spu基本信息pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();


        //保存spu的描述图片pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);

        //保存spu的图片集pms_spu_images
        List<String> images = spuSaveVo.getImages();
        if (!images.isEmpty()) {
            List<SpuImagesEntity> spuImagesEntities = images.stream().map(image -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                spuImagesEntity.setSpuId(spuId);
                spuImagesEntity.setImgUrl(image);
                return spuImagesEntity;
            }).collect(Collectors.toList());
            spuImagesService.saveBatch(spuImagesEntities);
        }


        //保存spu的规格参数pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        if (!baseAttrs.isEmpty()) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setSpuId(spuId)
                        .setAttrId(baseAttr.getAttrId())
                        .setAttrName(attrService.getObj(
                                Wrappers.lambdaQuery(AttrEntity.class)
                                        .eq(AttrEntity::getAttrId, baseAttr.getAttrId()),
                                String::valueOf
                        ))
                        .setAttrValue(baseAttr.getAttrValues())
                        .setQuickShow(baseAttr.getShowDesc());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(productAttrValueEntities);
        }

        //保存spu的积分信息mall_sms->sms_spu_bounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuId);
        //远程调用服务
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("保存spu的积分信息失败");
        }

        //保存当前spu对应的所有sku信息
        //          保存sku的基本信息pms_sku_info
        List<Skus> skus = spuSaveVo.getSkus();
        if (!skus.isEmpty()) {
            skus.forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                //获取默认图片
                sku.getImages().forEach(skuImage -> {
                    if (skuImage.getDefaultImg() == 1) {
                        skuInfoEntity.setSkuDefaultImg(skuImage.getImgUrl());
                    }
                });

                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId())
                        .setCatalogId(spuInfoEntity.getCatalogId())
                        .setSaleCount(0l)
                        .setSpuId(spuId);

                //保存sku的基本信息
                skuInfoService.save(skuInfoEntity);

                //保存后的skuId
                Long skuId = skuInfoEntity.getSkuId();
                //保存后的skuId


                //处理sku的图片信息
                List<Images> skuImages = sku.getImages();
                List<SkuImagesEntity> skuImagesEntities = skuImages.stream().map(skuImage -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId)
                            .setImgUrl(skuImage.getImgUrl())
                            .setDefaultImg(skuImage.getDefaultImg());
                    return skuImagesEntity;
                }).filter(item -> StringUtils.hasLength(item.getImgUrl())).collect(Collectors.toList());

                //保存sku的图片信息pms_sku_images
                skuImagesService.saveBatch(skuImagesEntities);

                //处理sku的销售属性
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

                //保存sku的销售属性pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);


                //保存sku的优惠、满减信息
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getDiscount().compareTo(BigDecimal.ZERO) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("保存sku的优惠、满减信息失败");
                    }
                }

            });
        }

    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String status = (String) params.get("status");

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                Wrappers.lambdaQuery(SpuInfoEntity.class)
                        .eq(StringUtils.hasLength(status), SpuInfoEntity::getPublishStatus, status)
                        .eq(StringUtils.hasLength(brandId), SpuInfoEntity::getBrandId, brandId)
                        .eq(StringUtils.hasLength(catelogId), SpuInfoEntity::getCatalogId, catelogId)
                        .and(StringUtils.hasLength(key), w -> {
                            w.eq(SpuInfoEntity::getId, key)
                                    .or()
                                    .like(SpuInfoEntity::getSpuName, key);
                        })
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void up(Long spuId) {

        List<SkuInfoEntity> skuInfoEntities = skuInfoService.list(
                Wrappers.lambdaQuery(SkuInfoEntity.class)
                        .eq(SkuInfoEntity::getSpuId, spuId)
        );

        BrandEntity brandEntity = brandService.getById(skuInfoEntities.get(0).getBrandId());
        CategoryEntity categoryEntity = categoryService.getById(skuInfoEntities.get(0).getCatalogId());
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrList(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.getSearchAttrIds(attrIds);
        List<SkuEsTo.AttrEsTo> attrEsTos = productAttrValueEntities
                .stream()
                .filter(item -> searchAttrIds.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsTo.AttrEsTo attrEsTo = new SkuEsTo.AttrEsTo();
                    BeanUtils.copyProperties(item, attrEsTo);
                    return attrEsTo;
                })
                .collect(Collectors.toList());

        Map<Long, Boolean> hasStockRsp = null;
        try {
            hasStockRsp = wareFeignService.hasStock(skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList())).getData();
        } catch (Exception e) {
            log.error("远程调用库存服务出现异常\n{}", e);
        }


        Map<Long, Boolean> finalHasStockRsp = hasStockRsp;
        List<SkuEsTo> skuEsToList = skuInfoEntities.stream().map(skuInfoEntity -> {
            SkuEsTo skuEsTo = new SkuEsTo();
            BeanUtils.copyProperties(skuInfoEntity, skuEsTo);
            skuEsTo.setSkuPrice(skuInfoEntity.getPrice());
            skuEsTo.setSkuImg(skuInfoEntity.getSkuDefaultImg());
            skuEsTo.setHotScore(0l);


            skuEsTo.setHasStock(finalHasStockRsp == null ? true : finalHasStockRsp.get(skuInfoEntity.getSkuId()));


            skuEsTo.setBrandName(brandEntity.getName());
            skuEsTo.setBrandImg(brandEntity.getLogo());

            skuEsTo.setCatalogId(categoryEntity.getCatId());
            skuEsTo.setCatalogName(categoryEntity.getName());

            skuEsTo.setAttrs(attrEsTos);

            return skuEsTo;
        }).collect(Collectors.toList());


        R up = searchFeignService.up(skuEsToList);

        if (up.getCode() == 0) {
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.UP.getCode());
            this.baseMapper.updateById(spuInfoEntity);
        }
    }

}