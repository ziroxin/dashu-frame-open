package com.kg.core.zapi.dto;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ziro
 * @date 2022-05-21 21:35:09
 */
@Getter
@Setter
public class ApiUserIdDTO implements BaseDTO {
    /**
     * 权限标记
     * 1. @PreAuthorize("hasAuthority('权限标记')") - Security 根据该标记，自动判断接口权限
     * 2. 路由权限标记 - vue前端菜单显示隐藏
     * 3. v-permission中的权限标记 - vue页面按钮/元素显示隐藏
     */
    private String apiPermission;
    /**
     * 角色ID
     */
    private String roleId;
}
