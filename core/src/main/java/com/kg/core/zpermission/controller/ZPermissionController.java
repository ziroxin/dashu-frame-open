package com.kg.core.zpermission.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kg.component.utils.GuidUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zpermission.dto.ZPermissionDTO;
import com.kg.core.zpermission.dto.ZRolePermissionDTO;
import com.kg.core.zpermission.entity.ZPermission;
import com.kg.core.zpermission.service.IZPermissionService;
import com.kg.core.zuser.entity.ZUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 资源权限表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-05-09
 */
@Api(tags = "ZPermissionController", value = "资源权限信息", description = "资源权限信息")
@RestController
@RequestMapping("permission")
public class ZPermissionController {

    @Autowired
    private IZPermissionService permissionService;

    @ApiOperation(value = "permission/add", notes = "添加资源（菜单，按钮等）", httpMethod = "POST")
    @ApiImplicitParams({
    })
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('permission:add')")
    public boolean add(@RequestBody ZPermission zPermission) throws BaseException {
        // 查询重复标记
        QueryWrapper<ZPermission> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ZPermission::getPermissionName, zPermission.getPermissionName());
        long count = permissionService.count(wrapper);
        if (count > 0) {
            throw new BaseException("资源标记已存在，请修改！");
        }
        // 保存
        zPermission.setPermissionId(GuidUtils.getUuid());
        zPermission.setCreateTime(LocalDateTime.now());
        if (permissionService.save(zPermission)) {
            return true;
        }
        return false;
    }

    @ApiOperation(value = "permission/update", notes = "修改资源（菜单，按钮等）", httpMethod = "POST")
    @ApiImplicitParams({
    })
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('permission:update')")
    public boolean update(@RequestBody ZPermission zPermission) throws BaseException {
        // 查询重复标记
        QueryWrapper<ZPermission> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ZPermission::getPermissionName, zPermission.getPermissionName())
                .ne(ZPermission::getPermissionId, zPermission.getPermissionId());
        long count = permissionService.count(wrapper);
        if (count > 0) {
            throw new BaseException("资源标记已存在，请修改！");
        }
        // 保存
        zPermission.setUpdateTime(LocalDateTime.now());
        if (permissionService.updateById(zPermission)) {
            return true;
        }
        return false;
    }

    @ApiOperation(value = "permission/updateParentId", notes = "修改上下级关系", httpMethod = "POST")
    @PostMapping("/updateParentId")
    @PreAuthorize("hasAuthority('permission:updateParentId')")
    public boolean updateParentId(@RequestBody ZPermission zPermission) {
        UpdateWrapper<ZPermission> wrapper = new UpdateWrapper<>();
        wrapper.lambda().set(ZPermission::getParentId, zPermission.getParentId())
                .eq(ZPermission::getPermissionId, zPermission.getPermissionId());
        if (permissionService.update(wrapper)) {
            return true;
        }
        return false;
    }

    @ApiOperation(value = "permission/delete", notes = "删除资源（菜单，按钮等）", httpMethod = "DELETE")
    @ApiImplicitParams({
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('permission:delete')")
    public boolean delete(@RequestBody String[] permissionIds) {
        if (permissionService.removeBatchByIds(Arrays.asList(permissionIds))) {
            return true;
        }
        return false;
    }

    @ApiOperation(value = "permission/tree/list", notes = "查询资源树表格", httpMethod = "GET")
    @ApiImplicitParams({
    })
    @GetMapping("/tree/list")
    @PreAuthorize("hasAuthority('permission:tree:list')")
    public List<ZPermissionDTO> treeList() {
        return permissionService.treeList();
    }

    @ApiOperation(value = "permission/listForRole", notes = "角色分配权限列表", httpMethod = "GET")
    @ApiImplicitParams({
    })
    @GetMapping("/listForRole")
    @PreAuthorize("hasAuthority('permission:listForRole')")
    public List<ZRolePermissionDTO> listForRole(String roleId) {
        return permissionService.listForRole(roleId);
    }

    @ApiOperation(value = "permission/treeList", notes = "查询资源树表格", httpMethod = "GET")
    @ApiImplicitParams({})
    @GetMapping("/treeList")
    @PreAuthorize("hasAuthority('permission:treeList')")
    public List<ZPermissionDTO> permissionTreeList() {
        return permissionService.permissionTreeList();
    }

    @ApiOperation(value = "permission/getListById", notes = "根据ID查询资源列表", httpMethod = "GET")
    @ApiImplicitParams({})
    @GetMapping("/getListById")
    @PreAuthorize("hasAuthority('permission:getListById')")
    public List<ZPermission> getListById(String permissionId) {
        return permissionService.getListById(permissionId);
    }

    @ApiOperation(value = "permission/user/all", notes = "查询当前用户所有资源权限", httpMethod = "GET")
    @GetMapping("user/all")
    public Map<String, Object> userAll() {
        Map<String, Object> map = new HashMap<>();
        ZUser currentUser = CurrentUserUtils.getCurrentUser();
        map.put("user", currentUser);
        map.putAll(permissionService.listPermissionByUserId(currentUser.getUserId()));
        return map;
    }


}
