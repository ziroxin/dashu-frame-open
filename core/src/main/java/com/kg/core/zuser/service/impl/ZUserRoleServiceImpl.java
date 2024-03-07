package com.kg.core.zuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.core.zrole.entity.ZRole;
import com.kg.core.zrole.mapper.ZRoleMapper;
import com.kg.core.zuser.entity.ZUserRole;
import com.kg.core.zuser.mapper.ZUserRoleMapper;
import com.kg.core.zuser.service.IZUserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
@Service
public class ZUserRoleServiceImpl extends ServiceImpl<ZUserRoleMapper, ZUserRole> implements IZUserRoleService {
    @Resource
    private ZRoleMapper roleMapper;

    /**
     * 查询用户的角色列表
     */
    @Override
    public List<ZRole> getRoleListByUserId(String userId) {
        return roleMapper.getRoleListByUserId(userId);
    }
}
