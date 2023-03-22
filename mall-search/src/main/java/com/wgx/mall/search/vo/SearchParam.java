package com.wgx.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wgx
 * @since 2023/3/21 14:11
 */
@Data
public class SearchParam {
    private String keyword;
    private Long catalog3Id;

    /**
     * 排序条件
     * saleCount_asc/saleCount_desc
     * skuPrice_asc/skuPrice_desc
     * hotScore_asc/hotScore_desc
     */
    private String sort;

    /**
     * 过滤条件
     */
    private Integer hasStock;//是否只显示有货
    private String skuPrice;//价格区间
    private List<Long> brandId;//品牌Id
    private List<String> attrs;//筛选属性
    private Integer pageNum;


}
