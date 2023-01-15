package com.kg.core.zpermission.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.zpermission.dto.ZPermissionApiDTO;
import com.kg.core.zpermission.entity.ZPermissionApi;
import com.kg.core.zpermission.service.IZPermissionApiService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 资源API关联表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
@RestController
@RequestMapping("permission/api")
@Validated
public class ZPermissionApiController {
    @Resource
    private IZPermissionApiService permissionApiService;


    @ApiOperation(value = "permission/api/getApiListByPermissionId", notes = "查询资源对应的API", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permissionId", value = "资源ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("getApiListByPermissionId")
    @PreAuthorize("hasAuthority('permission:api:getApiListByPermissionId')")
    public List<String> getApiListByPermissionId(String permissionId) {
        QueryWrapper<ZPermissionApi> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT *");
        wrapper.lambda().eq(ZPermissionApi::getPermissionId, permissionId);
        List<ZPermissionApi> list = permissionApiService.list(wrapper);
        if (list != null && list.size() > 0) {
            return list.stream().map(obj -> obj.getApiId()).collect(Collectors.toList());
        }
        return null;
    }

    @ApiOperation(value = "permission/api/savePermissionApi", notes = "保存资源和API关联关系", httpMethod = "POST")
    @PostMapping("savePermissionApi")
    @PreAuthorize("hasAuthority('permission:api:savePermissionApi')")
    @NoRepeatSubmit
    public void savePermissionApi(@RequestBody ZPermissionApiDTO permissionApiDTO) {
        permissionApiService.savePermissionApi(permissionApiDTO);
    }
}
