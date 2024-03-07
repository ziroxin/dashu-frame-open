package com.kg.core.zuser.service;

import com.kg.core.zrole.entity.ZRole;
import com.kg.core.zuser.entity.ZUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
public interface IZUserRoleService extends IService<ZUserRole> {


    /**
     * 查询用户的角色列表
     */
    List<ZRole> getRoleListByUserId(String userId);
}
