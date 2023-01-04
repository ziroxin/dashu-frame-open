package com.kg.core.zuser.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.PasswordRegexUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
import com.kg.core.zuser.dto.ZUserEditPasswordDTO;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.entity.ZUserRole;
import com.kg.core.zuser.mapper.ZUserMapper;
import com.kg.core.zuser.service.IZUserRoleService;
import com.kg.core.zuser.service.IZUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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

    @Override
    public Page<ZUserRoleSaveDTO> getUserRoleList(Integer page, Integer limit, String params) {
        Page<ZUserRoleSaveDTO> result = new Page<>(page, limit);
        JSONObject paramObj = JSONUtil.parseObj(params);
        paramObj.set("offset", (page - 1) * limit);
        paramObj.set("limit", limit);
        result.setRecords(zUserMapper.getUserRoleList(paramObj));
        result.setTotal(zUserMapper.getUserRoleCount(paramObj));
        return result;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean add(ZUserRoleSaveDTO zUserRoleSaveDTO) {
        ZUser zUser = new ZUser();
        BeanUtils.copyProperties(zUserRoleSaveDTO, zUser);
        zUser.setUserId(GuidUtils.getUuid());
        zUser.setCreateTime(LocalDateTime.now());

        ZUserRole zUserRole = new ZUserRole();
        zUserRole.setUserId(zUser.getUserId());
        zUserRole.setRoleId(zUserRoleSaveDTO.getRoleId());

        boolean s1 = save(zUser);
        boolean s2 = userRoleService.save(zUserRole);

        return (!s1 && !s2);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean update(ZUserRoleSaveDTO zUserRoleSaveDTO) {
        ZUser zUser = new ZUser();
        BeanUtils.copyProperties(zUserRoleSaveDTO, zUser);
        zUser.setUpdateTime(LocalDateTime.now());
        boolean s1 = updateById(zUser);

        boolean s2 = userRoleService.removeById(zUser.getUserId());

        ZUserRole zUserRole = new ZUserRole();
        zUserRole.setUserId(zUser.getUserId());
        zUserRole.setRoleId(zUserRoleSaveDTO.getRoleId());
        boolean s3 = userRoleService.save(zUserRole);

        return (!s1 && !s2 && !s3);
    }

    @Override
    public ZUserRoleSaveDTO getUserById(String userId) {
        return zUserMapper.getUserById(userId);
    }

    /**
     * 修改用户密码
     */
    @Override
    public void editPassword(ZUserEditPasswordDTO passwordDTO) throws BaseException {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
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
        System.out.println(safety.getBanUsername().intValue());
        if (1 == safety.getBanUsername().intValue()) {
            if (passwordDTO.getPassword().contains(user.getUserName())) {
                throw new BaseException("密码不能包含用户名！");
            }
        }
        // 修改密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        if (!updateById(user)) {
            throw new BaseException("修改密码失败！请重试");
        }
    }
}
