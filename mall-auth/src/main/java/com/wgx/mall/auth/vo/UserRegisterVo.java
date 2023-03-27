package com.wgx.mall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author wgx
 * @since 2023/3/27 15:56
 */
@Data
public class UserRegisterVo {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 6, max = 18, message = "用户名必须为6-18位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码必须为6-18位")
    private String password;

    @Pattern(regexp = "^1[0-9]{10}$", message = "手机号格式不对")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
