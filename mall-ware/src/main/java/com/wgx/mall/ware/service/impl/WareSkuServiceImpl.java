package com.wgx.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.utils.R;
import com.wgx.mall.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.ware.dao.WareSkuDao;
import com.wgx.mall.ware.entity.WareSkuEntity;
import com.wgx.mall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                Wrappers.lambdaQuery(WareSkuEntity.class)
                        .eq(StringUtils.hasLength(skuId), WareSkuEntity::getSkuId, skuId)
                        .eq(StringUtils.hasLength(wareId), WareSkuEntity::getWareId, wareId)
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities = this.list(
                Wrappers.lambdaQuery(WareSkuEntity.class)
                        .eq(skuId != null, WareSkuEntity::getSkuId, skuId)
                        .eq(wareId != null, WareSkuEntity::getWareId, wareId)
        );
        if (wareSkuEntities.isEmpty() || wareSkuEntities == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId)
                    .setWareId(wareId)
                    .setStock(skuNum)
                    .setStockLocked(0);
            try {
                R r = productFeignService.info(skuId);
                if (r.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) ((Map<String, Object>) r.get("skuInfo")).get("skuName"));
                }
            } catch (Exception e) {

            }

            this.save(wareSkuEntity);
        } else {
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public Map<Long, Boolean> hasStock(List<Long> skuIds) {
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.hasStock(skuIds);
        Map<Long, Boolean> map = new HashMap<>();
        wareSkuEntities.forEach(wareSkuEntity -> map.put(wareSkuEntity.getSkuId(), wareSkuEntity.getStock() > 0));
        return map;
    }

}