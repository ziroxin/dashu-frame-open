package com.kg.core.zuser.dto;

import com.kg.core.zrole.entity.ZRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户完整资料（包含所在部门和所属角色）
 *
 * @author ziro
 * @date 2024/3/7 10:05
 */
@Getter
@Setter
public class ZUserAllDTO extends ZUserDTO {
    /**
     * 所属角色列表
     */
    private List<ZRole> roleList;

    /**
     * 所在部门
     */
    private String orgName;

    /**
     * 部门父级ID
     */
    private String orgParentId;

    /**
     * 部门路径ID
     */
    private String orgPath;

    /**
     * 部门层级
     */
    private Integer orgLevel;
}
