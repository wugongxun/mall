package com.wgx.mall.product.dao;

import com.wgx.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wgx.mall.product.vo.AttrGroupRelationVo;
import com.wgx.mall.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 15:32:55
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    void deleteRelation(@Param("vos") AttrGroupRelationVo[] vos);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithSpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
