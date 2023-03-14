package com.wgx.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wgx
 * @since 2023/3/10 23:33
 */
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}
