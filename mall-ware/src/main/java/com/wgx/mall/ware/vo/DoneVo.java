package com.wgx.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wgx
 * @since 2023/3/11 17:07
 */
@Data
public class DoneVo {
    @NotNull
    private Long id;
    private List<DoneItem> items;

    @Data
    static public class DoneItem {
        private Long itemId;
        private Integer status;
        private String reason;
    }
}
