package com.wgx.mall.product.dao;

import com.wgx.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 15:32:55
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
