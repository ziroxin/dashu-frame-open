package com.kg.core.zuser.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户修改密码
 *
 * @author ziro
 * @date 2023-01-03 17:36:11
 */
@Getter
@Setter
public class ZUserEditPasswordDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 旧密码
     */
    private String oldPassword;
    /**
     * 新密码
     */
    private String password;
    /**
     * 是否默认密码
     */
    private Boolean isDefaultPassword;
}
