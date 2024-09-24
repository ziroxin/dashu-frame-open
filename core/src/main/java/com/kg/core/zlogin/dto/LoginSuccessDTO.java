package com.kg.core.zlogin.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ziro
 * @date 2022/5/2 22:17
 */
@Getter
@Setter
public class LoginSuccessDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // 登录成功信息
    private String successMsg;
    // 登录获得的Token
    private String accessToken;
    // Token有效期
    private Date accessTokenValidTime;
    // 是否默认密码（true：默认密码，false：非默认密码）
    private boolean isDefaultPassword;
    // 密码是否已过期失效（true：已过期，false：未过期）
    private boolean isInvalidPassword;
}
