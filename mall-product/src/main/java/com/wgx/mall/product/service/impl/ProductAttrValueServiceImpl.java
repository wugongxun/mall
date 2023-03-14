package com.wgx.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.ProductAttrValueDao;
import com.wgx.mall.product.entity.ProductAttrValueEntity;
import com.wgx.mall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrList(Long spuId) {
        return this.list(
                Wrappers.lambdaQuery(ProductAttrValueEntity.class)
                        .eq(ProductAttrValueEntity::getSpuId, spuId)
        );
    }

    @Override
    @Transactional
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        this.baseMapper.delete(
                Wrappers.lambdaQuery(ProductAttrValueEntity.class).eq(ProductAttrValueEntity::getSpuId, spuId)
        );

        for (ProductAttrValueEntity entity : entities) {
            entity.setSpuId(spuId);
        }
        this.saveBatch(entities);
    }

}