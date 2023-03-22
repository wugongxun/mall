package com.wgx.mall.search.vo;

import com.wgx.common.to.SkuEsTo;
import lombok.Data;

import java.util.List;

/**
 * @author wgx
 * @since 2023/3/21 14:45
 */
@Data
public class SearchResult {

    //查询的所有数据
    private List<SkuEsTo> products;

    private Integer pageNum;
    private Long total;
    private Integer totalPage;

    //查询结果所涉及到的所有品牌
    private List<BrandVo> brands;

    //查询结果所涉及到的所有属性
    private List<AttrVo> attrs;

    //查询结果所涉及到的所有分类
    private List<Catalog> catalogs;


    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class Catalog {
        private Long catalogId;
        private String catalogName;
    }

}
