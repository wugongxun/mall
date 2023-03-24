package com.wgx.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.constants.ProductConstant;
import com.wgx.mall.product.dao.AttrAttrgroupRelationDao;
import com.wgx.mall.product.entity.AttrAttrgroupRelationEntity;
import com.wgx.mall.product.entity.AttrEntity;
import com.wgx.mall.product.service.AttrService;
import com.wgx.mall.product.vo.AttrGroupRelationVo;
import com.wgx.mall.product.vo.AttrGroupWithAttrsVo;
import com.wgx.mall.product.vo.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.AttrGroupDao;
import com.wgx.mall.product.entity.AttrGroupEntity;
import com.wgx.mall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper();
        wrapper.eq(categoryId != 0, "catelog_id", categoryId)
                .and(StringUtils.hasLength(key), w -> {
                    w.eq("attr_group_id", key)
                            .or()
                            .like("attr_group_name", key);
                });
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        this.baseMapper.deleteRelation(vos);
    }

    @Override
    public PageUtils noRelation(Map<String, Object> params, Long attrgroupId) {
        //查出当前分组所属的分类
        AttrGroupEntity attrGroup = this.getById(attrgroupId);
        //查出这个分类下的所有分组的id
        List<Long> groupIds = this.baseMapper.selectList(
                Wrappers.lambdaQuery(AttrGroupEntity.class)
                        .eq(AttrGroupEntity::getCatelogId, attrGroup.getCatelogId())
        ).stream().mapToLong(AttrGroupEntity::getAttrGroupId).boxed().collect(Collectors.toList());
        //查出这些分组下所有关联的属性的id
        List<Long> attrIds = attrAttrgroupRelationDao.selectList(
                Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                        .in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds)
        ).stream().mapToLong(AttrAttrgroupRelationEntity::getAttrId).boxed().collect(Collectors.toList());
        //查出当前**分类**下的不在attrIds里的所有属性
        //      先判断是否有搜索关键字
        String key = (String) params.get("key");
        IPage<AttrEntity> page = attrService.page(
                new Query<AttrEntity>().getPage(params),
                Wrappers.lambdaQuery(AttrEntity.class)
                        .eq(AttrEntity::getCatelogId, attrGroup.getCatelogId())
                        .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())
                        .notIn(!attrIds.isEmpty(), AttrEntity::getAttrId, attrIds)
                        .and(StringUtils.hasLength(key), w -> {
                            w.eq(AttrEntity::getAttrId, key)
                                    .or()
                                    .like(AttrEntity::getAttrName, key);
                        })
        );
        return new PageUtils(page);
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntities = this.baseMapper.selectList(
                Wrappers.lambdaQuery(AttrGroupEntity.class)
                        .eq(AttrGroupEntity::getCatelogId, catelogId)
        );
        List<AttrGroupWithAttrsVo> res = new ArrayList<>(attrGroupEntities.size());
        if (!attrGroupEntities.isEmpty()) {
            res = attrGroupEntities.stream().map(attrGroupEntity -> {
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                BeanUtils.copyProperties(attrGroupEntity, attrGroupWithAttrsVo);

                List<AttrEntity> attrEntities = attrService.getAttrRelation(attrGroupEntity.getAttrGroupId());

                attrGroupWithAttrsVo.setAttrs(attrEntities);

                return attrGroupWithAttrsVo;
            }).collect(Collectors.toList());
        }
        return res;
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithSpuId(Long spuId, Long catalogId) {
        return this.baseMapper.getAttrGroupWithSpuId(spuId, catalogId);
    }

}