package com.kg.component.oauth2.client.dto;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Oauth2 - 用户绑定dto
 *
 * @author ziro
 * @date 2024/3/4 10:55
 */
@Getter
@Setter
public class Oauth2UserBindDTO implements BaseDTO {
    /**
     * 前端传递的缓存key（uuid）
     * 用于从redis中读取当前openId
     */
    private String loginId;
    /**
     * 待绑定的用户名
     */
    private String userName;
    /**
     * 待绑定的用户密码
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
    /**
     * 是否加密传输
     */
    private Boolean isEncrypt;
}
