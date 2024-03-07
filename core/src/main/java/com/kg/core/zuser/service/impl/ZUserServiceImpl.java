package com.kg.core.zuser.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.PasswordRegexUtils;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.zorg.entity.ZOrganization;
import com.kg.core.zorg.service.ZOrganizationService;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
import com.kg.core.zuser.dto.ZUserAllDTO;
import com.kg.core.zuser.dto.ZUserDTO;
import com.kg.core.zuser.dto.ZUserEditPasswordDTO;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.entity.ZUserRole;
import com.kg.core.zuser.mapper.ZUserMapper;
import com.kg.core.zuser.service.IZUserRoleService;
import com.kg.core.zuser.service.IZUserService;
import com.kg.core.zuserpassword.entity.ZUserPassword;
import com.kg.core.zuserpassword.service.ZUserPasswordService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-05-01
 */
@Service
public class ZUserServiceImpl extends ServiceImpl<ZUserMapper, ZUser> implements IZUserService {

    @Resource
    private ZUserMapper zUserMapper;
    @Resource
    private IZUserRoleService userRoleService;
    @Resource
    private ZSafetyService safetyService;
    @Resource
    private ZUserPasswordService passwordService;
    @Resource
    private ZOrganizationService organizationService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Page<ZUserDTO> getUserList(Integer page, Integer limit, String params) {
        Page<ZUserDTO> result = new Page<>(page, limit);
        JSONObject paramObj = JSONUtil.parseObj(params);
        paramObj.set("offset", (page - 1) * limit);
        paramObj.set("limit", limit);
        // 用户管理，排除开发管理员
        paramObj.set("developer", LoginConstant.DEVELOPER_USER_IDS.split(","));
        result.setRecords(zUserMapper.getUserRoleList(paramObj));
        result.setTotal(zUserMapper.getUserRoleCount(paramObj));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException {
        // 查重
        if (lambdaQuery().eq(ZUser::getUserName, zUserRoleSaveDTO.getUserName()).exists()) {
            throw new BaseException("用户名已存在!");
        }
        // 保存用户
        ZUser zUser = new ZUser();
        BeanUtils.copyProperties(zUserRoleSaveDTO, zUser);
        zUser.setUserId(GuidUtils.getUuid());
        // 密码是默认密码
        zUser.setPassword(passwordEncoder.encode(safetyService.getSafety().getDefaultPassword()));
        zUser.setCreateTime(LocalDateTime.now());
        save(zUser);
        // 保存用户角色
        for (String roleId : zUserRoleSaveDTO.getRoleId()) {
            ZUserRole zUserRole = new ZUserRole();
            zUserRole.setUserId(zUser.getUserId());
            zUserRole.setRoleId(roleId);
            userRoleService.save(zUserRole);
        }
        // 保存用户密码记录
        userPasswordUpdateLog(zUser);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException {
        // 查重
        if (lambdaQuery().eq(ZUser::getUserName, zUserRoleSaveDTO.getUserName())
                .ne(ZUser::getUserId, zUserRoleSaveDTO.getUserId()).exists()) {
            throw new BaseException("用户名已存在!");
        }
        ZUser zUser = new ZUser();
        BeanUtils.copyProperties(zUserRoleSaveDTO, zUser);
        zUser.setUpdateTime(LocalDateTime.now());
        updateById(zUser);

        userRoleService.removeById(zUser.getUserId());
        for (String roleId : zUserRoleSaveDTO.getRoleId()) {
            ZUserRole zUserRole = new ZUserRole();
            zUserRole.setUserId(zUser.getUserId());
            zUserRole.setRoleId(roleId);
            userRoleService.save(zUserRole);
        }
    }

    @Override
    public ZUserRoleSaveDTO getUserById(String userId) {
        return zUserMapper.getUserById(userId);
    }

    /**
     * 修改用户密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPassword(ZUserEditPasswordDTO passwordDTO) throws BaseException {
        ZUser user = getById(passwordDTO.getUserId());
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new BaseException("旧密码不正确！");
        }
        // 判断密码规则
        ZSafety safety = safetyService.getSafety();
        // 长度
        if (!PasswordRegexUtils.judgeLength(safety.getStartLength(), safety.getEndLength(), passwordDTO.getPassword())) {
            throw new BaseException(safety.getPrompt());
        }
        // 强度
        if (!PasswordRegexUtils.judgeRegex(safety.getLowercase().intValue(), safety.getUppercase().intValue(),
                safety.getNumbers().intValue(), safety.getSpecialCharacters().intValue(), passwordDTO.getPassword())) {
            throw new BaseException(safety.getPrompt());
        }
        // 是否包含用户名
        if (1 == safety.getBanUsername().intValue()) {
            if (passwordDTO.getPassword().contains(user.getUserName())) {
                throw new BaseException("密码不能包含用户名！");
            }
        }
        // 更新用户密码记录
        userPasswordUpdateLog(user);
        // 修改密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        if (!updateById(user)) {
            throw new BaseException("修改密码失败！请重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String[] userIds) throws BaseException {
        try {
            ZSafety safety = safetyService.getSafety();
            for (String userId : userIds) {
                ZUser user = getById(userId);
                // 更新用户密码记录
                userPasswordUpdateLog(user);
                // 更新用户表
                user.setPassword(passwordEncoder.encode(safety.getDefaultPassword()));
                user.setUpdateTime(LocalDateTime.now());
                updateById(user);
            }
        } catch (Exception e) {
            // 手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            throw new BaseException("重置密码失败！");
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(String[] userIds) {
        removeByIds(Arrays.asList(userIds));
        userRoleService.removeByIds(Arrays.asList(userIds));
    }

    /**
     * 更新用户密码记录
     */
    private void userPasswordUpdateLog(ZUser user) {
        // 移除旧密码记录
        passwordService.removeById(user.getUserId());
        // 添加新密码记录
        ZUserPassword userPassword = new ZUserPassword();
        userPassword.setUserId(user.getUserId());
        userPassword.setOldPassword(user.getPassword());
        userPassword.setEditPasswordTime(LocalDateTime.now());
        passwordService.save(userPassword);
    }

    @Override
    public ZUserAllDTO getUserAllInfo(String userId) {
        try {
            ZUser user = getById(userId);
            if (user != null) {
                ZUserAllDTO result = new ZUserAllDTO();
                // 查询部门信息
                ZOrganization org = organizationService.getById(user.getOrgId());
                if (org != null) {
                    result.setOrgName(org.getOrgName());
                }
                // 查询角色列表
                result.setRoleList(userRoleService.getRoleListByUserId(userId));
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
