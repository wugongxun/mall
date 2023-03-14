package com.wgx.mall.product.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wgx.common.constants.ProductConstant;
import com.wgx.mall.product.dao.AttrAttrgroupRelationDao;
import com.wgx.mall.product.dao.AttrGroupDao;
import com.wgx.mall.product.dao.CategoryDao;
import com.wgx.mall.product.entity.AttrAttrgroupRelationEntity;
import com.wgx.mall.product.entity.AttrGroupEntity;
import com.wgx.mall.product.entity.CategoryEntity;
import com.wgx.mall.product.service.CategoryService;
import com.wgx.mall.product.vo.AttrRespVo;
import com.wgx.mall.product.vo.AttrVo;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wgx.common.utils.PageUtils;
import com.wgx.common.utils.Query;

import com.wgx.mall.product.dao.AttrDao;
import com.wgx.mall.product.entity.AttrEntity;
import com.wgx.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        //保存attr基本属性
        this.baseMapper.insert(attrEntity);
        //保存关联关系
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId())
                    .setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long categoryId, String type) {
        String key = (String) params.get("key");
        Integer attrType = "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode();
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                Wrappers.lambdaQuery(AttrEntity.class)
                        .eq(categoryId != 0, AttrEntity::getCatelogId, categoryId)
                        .eq(StringUtils.hasLength(type), AttrEntity::getAttrType, attrType)
                        .and(StringUtils.hasLength(key), w -> {
                            w.eq(AttrEntity::getAttrId, key)
                                    .or()
                                    .like(AttrEntity::getAttrName, key);
                        })
        );
        List<AttrRespVo> collect = page.getRecords().stream().map(item -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(item, attrRespVo);
            //设置分组信息
            if (attrType == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                        Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                                .select(AttrAttrgroupRelationEntity::getAttrGroupId)
                                .eq(AttrAttrgroupRelationEntity::getAttrId, item.getAttrId())
                );
                if (attrAttrgroupRelationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(
                            Wrappers.lambdaQuery(AttrGroupEntity.class)
                                    .select(AttrGroupEntity::getAttrGroupName)
                                    .eq(AttrGroupEntity::getAttrGroupId, attrAttrgroupRelationEntity.getAttrGroupId())
                    );
                    attrRespVo.setAttrGroupName(attrGroupEntity != null ? attrGroupEntity.getAttrGroupName() : "");
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(item.getCatelogId());
            attrRespVo.setCategoryName(categoryEntity == null ? "" : categoryEntity.getName());
            return attrRespVo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(collect);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        //设置分组信息
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                    Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                            .select(AttrAttrgroupRelationEntity::getAttrGroupId)
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attrRespVo.getAttrId())
            );
            if (attrAttrgroupRelationEntity != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(
                        Wrappers.lambdaQuery(AttrGroupEntity.class)
                                .select(AttrGroupEntity::getAttrGroupName)
                                .eq(AttrGroupEntity::getAttrGroupId, attrAttrgroupRelationEntity.getAttrGroupId())
                );
                attrRespVo.setAttrGroupName(attrGroupEntity != null ? attrGroupEntity.getAttrGroupName() : "");
            }
        }
        attrRespVo.setCatelogPath(categoryService.findCategoryPath(attrRespVo.getCatelogId()));
        return attrRespVo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);

        //更新或添加分组信息
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrVo.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            Integer count = attrAttrgroupRelationDao.selectCount(
                    Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attrAttrgroupRelationEntity.getAttrId())
            );
            if (count > 0) {
                attrAttrgroupRelationDao.update(
                        attrAttrgroupRelationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>()
                                .set("attr_group_id", attrAttrgroupRelationEntity.getAttrGroupId())
                                .eq("attr_id", attrAttrgroupRelationEntity.getAttrId())

                );
            } else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> getAttrRelation(Long attrGroupId) {
        List<Long> attrIds = attrAttrgroupRelationDao.selectObjs(
                Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                        .select(AttrAttrgroupRelationEntity::getAttrId)
                        .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId)
        ).stream().map(o -> (Long) o).collect(Collectors.toList());
        return attrIds.isEmpty() ? null : this.baseMapper.selectBatchIds(attrIds);
    }

    @Override
    public List<Long> getSearchAttrIds(List<Long> attrIds) {
        return this.baseMapper.getSearchAttrIds(attrIds);
    }

}