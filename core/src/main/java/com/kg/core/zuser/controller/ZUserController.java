package com.kg.core.zuser.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zorg.dto.ZOrganizationTreeSelectDTO;
import com.kg.core.zorg.service.ZOrganizationService;
import com.kg.core.zuser.dto.ZUserDTO;
import com.kg.core.zuser.dto.ZUserEditPasswordDTO;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.dto.ZUserStatusDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

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
    private ZOrganizationService organizationService;

    @ApiOperation(value = "/user/list", notes = "查询用户列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('user:list')")
    public Page<ZUserDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return userService.getUserList(page, limit, params);
    }

    @ApiOperation(value = "/user/getById", notes = "用户详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('user:getById')")
    public ZUserRoleSaveDTO getById(String userId) {
        return userService.getUserById(userId);
    }

    @ApiOperation(value = "/user/org/tree", notes = "用户管理组织机构树", httpMethod = "GET")
    @ApiImplicitParams({})
    @GetMapping("/org/tree")
    @PreAuthorize("hasAuthority('user:org:tree')")
    public List<ZOrganizationTreeSelectDTO> orgTree() {
        return organizationService.treeForSelect();
    }

    @ApiOperation(value = "/user/add", notes = "添加用户", httpMethod = "POST")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/user/add", logMsg = "添加用户")
    public void add(@RequestBody ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException {
        try {
            userService.add(zUserRoleSaveDTO);
        } catch (BaseException e) {
            throw new BaseException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("添加用户失败!");
        }
    }

    @ApiOperation(value = "/user/update", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    @PreAuthorize("hasAuthority('user:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/user/update", logMsg = "修改用户信息")
    public void update(@RequestBody ZUserRoleSaveDTO zUserRoleSaveDTO) throws BaseException {
        try {
            userService.update(zUserRoleSaveDTO);
        } catch (BaseException e) {
            throw new BaseException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("修改用户信息失败");
        }
    }

    @ApiOperation(value = "/user/delete", notes = "批量删除用户", httpMethod = "POST")
    @PostMapping("delete")
    @PreAuthorize("hasAuthority('user:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/user/delete", logMsg = "删除用户")
    public void delete(@RequestBody String[] userIds) throws BaseException {
        try {
            userService.delete(userIds);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("删除用户信息失败！");
        }
    }

    @ApiOperation(value = "/user/reset/password", notes = "批量重置密码", httpMethod = "POST")
    @PostMapping("reset/password")
    @PreAuthorize("hasAuthority('user:reset:password')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/user/reset/password", logMsg = "重置密码")
    public void resetPassword(@RequestBody String[] userIds) throws BaseException {
        userService.resetPassword(userIds);
    }

    @ApiOperation(value = "/user/edit/password", notes = "修改用户密码", httpMethod = "POST")
    @PostMapping("edit/password")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/user/edit/password", logMsg = "修改用户密码")
    public void editPassword(@RequestBody ZUserEditPasswordDTO passwordDTO) throws BaseException {
        if (!StringUtils.hasText(passwordDTO.getUserId())) {
            // 不传userId，则修改当前用户的密码
            passwordDTO.setUserId(CurrentUserUtils.getCurrentUser().getUserId());
        }
        userService.editPassword(passwordDTO);
    }

    @ApiOperation(value = "/user/change/status", notes = "启用/禁用用户", httpMethod = "POST")
    @PostMapping("change/status")
    @PreAuthorize("hasAuthority('user:change:status')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/user/change/status", logMsg = "启用/禁用用户")
    public void changeStatus(@RequestBody ZUserStatusDTO userStatusDTO) throws BaseException {
        boolean update = userService.lambdaUpdate().set(ZUser::getStatus, userStatusDTO.getStatus())
                .in(ZUser::getUserId, Arrays.asList(userStatusDTO.getUserIds())).update();
        if (!update) {
            throw new BaseException("用户状态更新失败！");
        }
    }
}
