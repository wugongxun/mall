package com.wgx.mall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.wgx.mall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wgx
 * @since 2023/3/10 17:10
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 该分组下的所有属性
     */
    private List<AttrEntity> attrs;
}
