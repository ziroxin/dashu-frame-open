package com.kg.core.zlogin.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录表单DTO
 *
 * @author ziro
 * @date 2023-01-03 10:18:34
 */
@Getter
@Setter
public class LoginFormDTO {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码
     */
    private String yzm;
    /**
     * 验证码uuid
     */
    private String codeUuid;
}
