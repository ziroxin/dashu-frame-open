package com.kg.core.zrole.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.core.zrole.entity.ZRole;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
public interface ZRoleMapper extends BaseMapper<ZRole> {

    /**
     * 查询用户的角色列表
     */
    List<ZRole> getRoleListByUserId(String userId);
}
