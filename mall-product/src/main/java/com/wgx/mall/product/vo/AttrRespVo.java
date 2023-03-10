package com.wgx.mall.product.vo;

import lombok.Data;

/**
 * @author wgx
 * @since 2023/3/9 15:57
 */
@Data
public class AttrRespVo extends AttrVo{
    private String attrGroupName;
    private String categoryName;
    private Long[] catelogPath;
}
