package com.wgx.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.wgx.common.validator.ListValue;
import com.wgx.common.validator.group.AddGroup;
import com.wgx.common.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author wgx
 * @email 2297665453@qq.com
 * @date 2023-03-02 15:32:55
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "品牌id必须为空", groups = AddGroup.class)
	@NotNull(message = "品牌id不能为空", groups = UpdateGroup.class)
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups = AddGroup.class)
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "品牌logo地址不能为空", groups = AddGroup.class)
	@URL(message = "品牌logo地址必须为url", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "显示状态不能为空", groups = AddGroup.class)
	@ListValue(values = {0, 1}, groups = {AddGroup.class, UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "首字母不能为空", groups = AddGroup.class)
	@Pattern(regexp = "^[a-zA-Z]?$", message = "首字母必须单个英文字母", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空", groups = AddGroup.class)
	@Digits(integer = 10, fraction = 0, message = "排序必须为正整数", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
