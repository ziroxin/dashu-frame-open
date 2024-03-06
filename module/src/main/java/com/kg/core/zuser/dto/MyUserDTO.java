package com.kg.core.zuser.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户个人中心接口实体类
 *
 * @author ziro
 * @date 2024/3/5 14:37
 */
@Getter
@Setter
public class MyUserDTO extends ZUserRoleSaveDTO {
    /**
     * 用户部门
     */
    private String orgName;
    /**
     * 是否绑定Oauth2的openId
     */
    private boolean isBind;
}
