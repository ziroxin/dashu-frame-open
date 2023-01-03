package com.kg.core.zuser.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.exception.BaseException;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserRoleService;
import com.kg.core.zuser.service.IZUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-05-01
 */
@Api(tags = "ZUserController", value = "用户信息", description = "用户信息")
@RestController
@RequestMapping("user")
public class ZUserController {

    @Resource
    private IZUserService userService;
    @Resource
    private IZUserRoleService userRoleService;
    @Resource
    private ZSafetyService safetyService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @ApiOperation(value = "user/list", notes = "查询用户列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('user:list')")
    public Page<ZUserRoleSaveDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                       @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                       @RequestParam(value = "params", required = false) String params) {
        return userService.getUserRoleList(page, limit, params);
    }

    @ApiOperation(value = "user/getById", notes = "用户详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('user:getById')")
    public ZUserRoleSaveDTO getById(String userId) {
        return userService.getUserById(userId);
    }

    @ApiOperation(value = "user/add", notes = "添加用户", httpMethod = "POST")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:add')")
    public void add(@RequestBody ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException {
        boolean s1 = userService.add(zUserRoleSaveDTO);
        if (s1) {
            throw new BaseException("添加用户失败!");
        }
    }

    @ApiOperation(value = "user/update", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    @PreAuthorize("hasAuthority('user:update')")
    public void update(@RequestBody ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException {
        boolean s1 = userService.update(zUserRoleSaveDTO);
        if (s1) {
            throw new BaseException("修改用户信息失败");
        }
    }

    @ApiOperation(value = "user/delete", notes = "批量删除用户", httpMethod = "DELETE")
    @DeleteMapping("delete")
    @PreAuthorize("hasAuthority('user:delete')")
    public void delete(@RequestBody String[] userIds) throws BaseException {
        boolean s1 = userService.removeByIds(Arrays.asList(userIds));
        boolean s2 = userRoleService.removeByIds(Arrays.asList(userIds));
        if (!s1 && !s2) {
            throw new BaseException("删除用户失败！");
        }
    }

    @ApiOperation(value = "user/reset/password", notes = "批量重置密码", httpMethod = "POST")
    @PostMapping("reset/password")
    @PreAuthorize("hasAuthority('user:reset:password')")
    public void resetPassword(@RequestBody String[] userIds) throws BaseException {
        try {
            ZSafety safety = safetyService.getSafety();
            for (String userId : userIds) {
                ZUser user = new ZUser();
                user.setUserId(userId);
                user.setPassword(passwordEncoder.encode(safety.getDefaultPassword()));
                userService.updateById(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("重置密码失败！");
        }
    }
}
