package com.kg.core.zrole.controller;


import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.zrole.dto.ZRolePermissionSaveDTO;
import com.kg.core.zrole.service.IZRolePermissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色权限表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
@RestController
@RequestMapping("role/permission")
public class ZRolePermissionController {
    @Autowired
    private IZRolePermissionService rolePermissionService;

    @ApiOperation(value = "role/permission/saveRolePermission", notes = "保存角色权限关系", httpMethod = "POST")
    @PostMapping("saveRolePermission")
    @PreAuthorize("hasAuthority('role:permission:saveRolePermission')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/role/permission/saveRolePermission", logMsg = "保存角色权限关系")
    public void saveRolePermission(@RequestBody ZRolePermissionSaveDTO rolePermissionSaveDTO) {
        rolePermissionService.saveRolePermission(rolePermissionSaveDTO);
    }
}
