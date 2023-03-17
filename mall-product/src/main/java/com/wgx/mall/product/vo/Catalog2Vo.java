package com.wgx.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author wgx
 * @since 2023/3/16 14:12
 */
@Data
@Accessors(chain = true)
public class Catalog2Vo {
    private String catalog1Id;
    private List<Catalog3Vo> catalog3List;
    private String id;
    private String name;

    @Data
    @Accessors(chain = true)
    public static class Catalog3Vo {
        private String catalog2Id;
        private String id;
        private String name;
    }
}
