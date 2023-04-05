package com.wgx.common.to;

import lombok.Data;

/**
 * @author wgx
 * @since 2023/4/1 16:05
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private Boolean tempUser = false;
}
