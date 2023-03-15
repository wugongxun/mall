package com.wgx.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.CategoryDao;
import com.wgx.mall.product.entity.CategoryEntity;
import com.wgx.mall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> tree() {
        //查出所有分类
        List<CategoryEntity> entities = this.baseMapper.selectList(null);
        return entities.stream().filter(item -> item.getCatLevel() == 1)
                .map(item -> item.setChildren(getChildren(item, entities)))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> entities) {
        return entities.stream().filter(item -> item.getParentCid().equals(root.getCatId()))
                .map(item -> item.setChildren(getChildren(item, entities)))
                .sorted()
                .collect(Collectors.toList());
    }

    //删除
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        //TODO 删除前判断
        this.baseMapper.deleteBatchIds(idList);
        return false;
    }

    //根据catelogId查找从一级分类开始的路径
    @Override
    public Long[] findCategoryPath(Long catelogId) {
        Deque<Long> catelogPath = new LinkedList();
        catelogPath.addFirst(catelogId);
        CategoryEntity category = this.getById(catelogId);
        while (category.getParentCid() != 0) {
            category = this.getById(category.getParentCid());
            catelogPath.addFirst(category.getCatId());
        }
        return catelogPath.stream().toArray(Long[]::new);
    }

    @Override
    public List<CategoryEntity> getLevel1Categroies() {
        return this.baseMapper.selectList(
                Wrappers.lambdaQuery(CategoryEntity.class)
                        .eq(CategoryEntity::getParentCid, 0)
        );
    }
}