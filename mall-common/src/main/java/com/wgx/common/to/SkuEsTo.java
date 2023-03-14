package com.wgx.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wgx
 * @since 2023/3/14 15:20
 */
@Data
public class SkuEsTo {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<AttrEsTo> attrs;

    @Data
    public static class AttrEsTo {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
