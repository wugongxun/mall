package com.wgx.mall.product.vo;

import com.wgx.mall.product.entity.SkuImagesEntity;
import com.wgx.mall.product.entity.SkuInfoEntity;
import com.wgx.mall.product.entity.SpuInfoDescEntity;
import com.wgx.mall.product.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wgx
 * @since 2023/3/24 15:38
 */
@Data
public class SkuItemVo {
    private SkuInfoEntity skuInfo;
    private Boolean hasStock;
    private List<SkuImagesEntity> images;
    private SpuInfoDescEntity spuInfo;
    private List<SkuItemSaleAttrsVo> saleAttrs;
    private List<SpuItemAttrGroupVo> groupAttrs;

    @Data
    public static class SkuItemSaleAttrsVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdsVo> attrValues;

    }

    @Data
    public static class SpuItemAttrGroupVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    @Data
    public static class SpuBaseAttrVo {
        private String attrName;
        private String attrValue;
    }

    @Data
    public static class AttrValueWithSkuIdsVo {
        private String attrValue;
        private String skuIds;
    }
}





