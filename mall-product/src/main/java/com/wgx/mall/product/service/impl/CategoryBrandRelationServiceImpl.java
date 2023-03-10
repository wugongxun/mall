package com.wgx.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.mall.product.dao.BrandDao;
import com.wgx.mall.product.entity.BrandEntity;
import com.wgx.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.CategoryBrandRelationDao;
import com.wgx.mall.product.entity.CategoryBrandRelationEntity;
import com.wgx.mall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<BrandEntity> relationBrandsList(Long catId) {
        List<Long> brandIds = this.baseMapper.selectObjs(
                Wrappers.lambdaQuery(CategoryBrandRelationEntity.class)
                        .select(CategoryBrandRelationEntity::getBrandId)
                        .eq(CategoryBrandRelationEntity::getCatelogId, catId)
        ).stream().mapToLong(i -> (long) i).boxed().collect(Collectors.toList());
        return brandService.listByIds(brandIds);
    }

}