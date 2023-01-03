package com.kg.core.zuser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-05-01
 */
public interface IZUserService extends IService<ZUser> {

    Page<ZUserRoleSaveDTO> getUserRoleList(Integer page, Integer limit, String params);

    boolean add(ZUserRoleSaveDTO zUserRoleSaveDTO);

    boolean update(ZUserRoleSaveDTO zUserRoleSaveDTO);

    /**
     * 查询用户详情
     *
     * @param userId 用户id
     * @return 用户详情DTO
     */
    ZUserRoleSaveDTO getUserById(String userId);
}
