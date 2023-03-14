package com.wgx.mall.product.dao;

import com.wgx.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 15:32:55
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> getSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
