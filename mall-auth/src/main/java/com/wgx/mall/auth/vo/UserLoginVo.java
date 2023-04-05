package com.wgx.mall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author wgx
 * @since 2023/3/28 15:44
 */
@Data
public class UserLoginVo {

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;

}
