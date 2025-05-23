package com.kg.core.zuser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.exception.BaseException;
import com.kg.core.zuser.dto.ZUserAllDTO;
import com.kg.core.zuser.dto.ZUserDTO;
import com.kg.core.zuser.dto.ZUserEditPasswordDTO;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-05-01
 */
public interface IZUserService extends IService<ZUser> {

    Page<ZUserDTO> getUserList(Integer page, Integer limit, String params);

    void add(ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException;

    void update(ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException;

    /**
     * 查询用户详情
     *
     * @param userId 用户id
     * @return 用户详情DTO
     */
    ZUserRoleSaveDTO getUserById(String userId);

    /**
     * 修改用户密码
     */
    void editPassword(ZUserEditPasswordDTO passwordDTO) throws BaseException;

    /**
     * 重置密码
     */
    void resetPassword(String[] userIds) throws BaseException;

    /**
     * 批量删除用户
     *
     * @param userIds 用户id数组
     */
    void delete(String[] userIds);

    /**
     * 获取完整用户信息（包含用户角色、部门信息）
     */
    ZUserAllDTO getUserAllInfo(String userId);


    /**
     * 列出子用户
     *
     * @param orgId         组织id
     * @param isIncludeSelf 是否包括自己所在的部门
     * @return {@link List }<{@link ZUser }>
     */
    List<ZUser> listChildrenUsers(String orgId, boolean isIncludeSelf);
}
