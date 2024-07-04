package com.kg.module.message.controller;

import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zorg.dto.ZOrganizationDTO;
import com.kg.core.zorg.service.ZOrganizationService;
import com.kg.core.zrole.entity.ZRole;
import com.kg.core.zrole.service.IZRoleService;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import com.kg.module.message.enums.ScopeEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ziro
 * @date 2024/6/29 15:40
 */
@RestController
@RequestMapping("/message/to")
public class ZMessageToUserController {
    @Resource
    private IZUserService userService;
    @Resource
    private ZOrganizationService orgService;
    @Resource
    private IZRoleService roleService;

    @ApiOperation(value = "/message/to/user/list", notes = "发送消息：用户列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "用户列表查询范围", value = "scope", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/user/list")
    public List<Map<String, Object>> userList(String scope) {
        List<ZUser> users = new ArrayList<>();
        if (scope.equals(ScopeEnum.ALL.getCode())) {
            // 全部用户
            users = userService.list();
        } else if (scope.equals(ScopeEnum.CHILDREN.getCode())) {
            // 当前用户下级部门用户（不包含自己所在的部门）
            users = userService.listChildrenUsers(CurrentUserUtils.getCurrentUser().getOrgId(), false);
        } else if (scope.equals(ScopeEnum.SELF_AND_CHILDREN.getCode())) {
            // 当前用户下级部门用户（包含自己所在的部门）
            users = userService.listChildrenUsers(CurrentUserUtils.getCurrentUser().getOrgId(), true);
        }
        return users.stream()
                .map(user -> {
                    // 只返回userId和userName
                    HashMap<String, Object> newUser = new HashMap<>();
                    newUser.put("userId", user.getUserId());
                    newUser.put("userName", user.getUserName());
                    return newUser;
                }).collect(Collectors.toList());
    }

    @ApiOperation(value = "/message/to/org/tree", notes = "发送消息：组织机构树", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "组织机构查询范围", value = "scope", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/org/tree")
    public List<ZOrganizationDTO> orgTree(String scope) {
        List<ZOrganizationDTO> tree = new ArrayList<>();
        if (scope.equals(ScopeEnum.ALL.getCode())) {
            // 全部组织机构
            tree = orgService.tree(null, null);
        } else if (scope.equals(ScopeEnum.CHILDREN.getCode())) {
            // 当前用户下级部门组织机构（不包含自己所在的部门）
            String orgId = CurrentUserUtils.getCurrentUser().getOrgId();
            tree = orgService.tree(null, orgId);
            if (tree.size() > 0) {
                tree = tree.get(0).getChildren();
            }
        } else if (scope.equals(ScopeEnum.SELF_AND_CHILDREN.getCode())) {
            // 当前用户下级部门组织机构（包含自己所在的部门）
            tree = orgService.tree(null, CurrentUserUtils.getCurrentUser().getOrgId());
        }
        return tree;
    }

    @ApiOperation(value = "/message/to/role/list", notes = "发送消息：角色列表", httpMethod = "GET")
    @ApiImplicitParams({})
    @GetMapping("/role/list")
    public List<ZRole> roleList() {
        return roleService.lambdaQuery().orderByAsc(ZRole::getRoleOrder).list();
    }
}
