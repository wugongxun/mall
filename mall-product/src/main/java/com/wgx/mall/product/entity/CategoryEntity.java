package com.wgx.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品三级分类
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 15:32:55
 */
@Data
@TableName("pms_category")
@Accessors(chain = true)
public class CategoryEntity implements Serializable, Comparable<CategoryEntity> {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId
	private Long catId;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 父分类id
	 */
	private Long parentCid;
	/**
	 * 层级
	 */
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 */
	@TableLogic
	private Integer showStatus;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;
	/**
	 * 子分类集合
	 */
	@TableField(exist = false)
	@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
	private List<CategoryEntity> children;

	@Override
	public int compareTo(CategoryEntity o) {
		return Optional.ofNullable(this.sort).orElse(Integer.MAX_VALUE) -
				Optional.ofNullable(o.sort).orElse(Integer.MAX_VALUE);
	}
}
