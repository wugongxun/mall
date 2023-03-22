package com.wgx.mall.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.mall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.CategoryDao;
import com.wgx.mall.product.entity.CategoryEntity;
import com.wgx.mall.product.service.CategoryService;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

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
    @Cacheable(cacheNames = "category", key = "'level1Categroies'")
    public List<CategoryEntity> getLevel1Categroies() {
        return this.baseMapper.selectList(
                Wrappers.lambdaQuery(CategoryEntity.class)
                        .eq(CategoryEntity::getParentCid, 0)
        );
    }

    @Override
    @Cacheable(cacheNames = "category", key = "'catalogJson'", sync = true)
    public Map<String, List<Catalog2Vo>> getCatalogJson() {

//        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
//        if (StringUtils.hasLength(catalogJson)) {
//            //缓存命中
//            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>(){});
//        }

        //加锁
        RLock lock = redissonClient.getLock("getCatalogJsonLock");
        lock.lock(10, TimeUnit.SECONDS);


        try {

            //再次检查缓存
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if (StringUtils.hasLength(catalogJson)) {
                //缓存命中
                return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>(){});
            }

            List<CategoryEntity> categoryEntities = this.list();
            Map<String, List<Catalog2Vo>> res = categoryEntities.stream()
                    .filter(categoryEntity1 -> categoryEntity1.getParentCid() == 0)
                    .collect(Collectors.toMap(k -> k.getCatId().toString(), v ->
                            categoryEntities.stream()
                                    .filter(categoryEntity2 -> categoryEntity2.getParentCid() == v.getCatId())
                                    .map(categoryEntity2 -> {
                                        Catalog2Vo catalog2Vo = new Catalog2Vo();
                                        catalog2Vo.setCatalog1Id(v.getCatId().toString())
                                                .setId(categoryEntity2.getCatId().toString())
                                                .setName(categoryEntity2.getName())
                                                .setCatalog3List(
                                                        categoryEntities.stream()
                                                                .filter(categoryEntity3 -> categoryEntity3.getParentCid() == categoryEntity2.getCatId())
                                                                .map(categoryEntity3 -> {
                                                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo();
                                                                    catalog3Vo.setCatalog2Id(categoryEntity2.getCatId().toString())
                                                                            .setName(categoryEntity3.getName())
                                                                            .setId(categoryEntity3.getCatId().toString());
                                                                    return catalog3Vo;
                                                                })
                                                                .collect(Collectors.toList())
                                                );
                                        return catalog2Vo;
                                    }).collect(Collectors.toList())
                    ));
//            //存入redis
//            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(res), 1, TimeUnit.DAYS);
            return res;
        } finally {
            lock.unlock();
        }


//        List<CategoryEntity> level1Categroies = this.getLevel1Categroies();
//        return level1Categroies.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            List<CategoryEntity> categoryEntities = this.baseMapper.selectList(
//                    Wrappers.lambdaQuery(CategoryEntity.class)
//                            .eq(CategoryEntity::getParentCid, v.getCatId())
//            );
//            return categoryEntities.stream().map(category2Entity -> {
//                Catalog2Vo catalog2Vo = new Catalog2Vo();
//                catalog2Vo.setCatalog1Id(v.getCatId().toString())
//                        .setId(category2Entity.getCatId().toString())
//                        .setName(category2Entity.getName())
//                        .setCatalog3List(
//                                this.baseMapper.selectList(
//                                        Wrappers.lambdaQuery(CategoryEntity.class)
//                                                .eq(CategoryEntity::getParentCid, category2Entity.getCatId())
//                                ).stream().map(category3Entity -> {
//                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo();
//                                    catalog3Vo.setCatalog2Id(category2Entity.getCatId().toString())
//                                            .setName(category3Entity.getName())
//                                            .setId(category3Entity.getCatId().toString());
//                                    return catalog3Vo;
//                                }).collect(Collectors.toList())
//                        );
//                return catalog2Vo;
//            }).collect(Collectors.toList());
//        }));
    }
}