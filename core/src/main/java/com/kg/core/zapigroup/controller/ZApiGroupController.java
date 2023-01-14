package com.kg.core.zapigroup.controller;


import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.zapi.service.IZApiService;
import com.kg.core.zapigroup.dto.ZApiGroupDTO;
import com.kg.core.zapigroup.entity.ZApiGroup;
import com.kg.core.zapigroup.service.IZApiGroupService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * API分组信息表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-05-17
 */
@RestController
@RequestMapping("/api/group")
public class ZApiGroupController {
    @Resource
    private IZApiService apiService;
    @Resource
    private IZApiGroupService groupService;

    @ApiOperation(value = "/api/group/list", notes = "查询分组列表", httpMethod = "GET")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('api:group:list')")
    public List<ZApiGroup> list() {
        return groupService.lambdaQuery().orderByAsc(ZApiGroup::getGroupOrder).list();
    }

    @ApiOperation(value = "/api/group/delete", notes = "删除分组信息", httpMethod = "POST")
    @PostMapping("delete")
    @PreAuthorize("hasAuthority('api:group:delete')")
    @NoRepeatSubmit
    public boolean delete(String apiGroupId) {
        return apiService.deletApiGroup(apiGroupId);
    }

    @ApiOperation(value = "/api/group/add", notes = "添加分组信息", httpMethod = "POST")
    @PostMapping("add")
    @PreAuthorize("hasAuthority('api:group:add')")
    @NoRepeatSubmit
    public void add(@RequestBody ZApiGroupDTO zApiGroupDTO) {
        groupService.add(zApiGroupDTO);
    }

}
