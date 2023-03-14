package com.wgx.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wgx
 * @since 2023/3/10 23:40
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
