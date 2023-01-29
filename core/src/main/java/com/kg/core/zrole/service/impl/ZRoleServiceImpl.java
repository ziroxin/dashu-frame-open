package com.kg.core.zrole.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.utils.GuidUtils;
import com.kg.core.zrole.entity.ZRole;
import com.kg.core.zrole.entity.ZRolePermission;
import com.kg.core.zrole.mapper.ZRoleMapper;
import com.kg.core.zrole.service.IZRolePermissionService;
import com.kg.core.zrole.service.IZRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
@Service
public class ZRoleServiceImpl extends ServiceImpl<ZRoleMapper, ZRole> implements IZRoleService {
    @Resource
    private IZRolePermissionService rolePermissionService;

    /**
     * 复制角色
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void copy(String roleId) {
        // 复制角色
        ZRole role = getById(roleId);
        role.setRoleId(GuidUtils.getUuid());
        role.setRoleName(role.getRoleName() + "-COPY");
        save(role);
        // 复制角色对应权限
        List<ZRolePermission> list = rolePermissionService.lambdaQuery().eq(ZRolePermission::getRoleId, roleId).list();
        if (list != null && list.size() > 0) {
            list.stream().forEach(r -> r.setRoleId(role.getRoleId()));
            rolePermissionService.saveBatch(list);
        }
    }
}
