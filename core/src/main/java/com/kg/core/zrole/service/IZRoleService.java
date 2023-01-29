package com.kg.core.zrole.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.zrole.entity.ZRole;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
public interface IZRoleService extends IService<ZRole> {

    /**
     * 复制角色
     */
    void copy(String roleId);
}
