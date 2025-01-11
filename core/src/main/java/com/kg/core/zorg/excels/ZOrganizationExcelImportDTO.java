package com.kg.core.zorg.excels;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导入Excel实体 - 组织机构表
 *
 * @author ziro
 * @date 2023-01-11
 */
@Getter
@Setter
public class ZOrganizationExcelImportDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 组织机构名称
     */
    private String orgName;

    /**
     * 父级ID
     */
    private String orgParentId;

    /**
     * 顺序
     */
    private String orderIndex;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 组织机构ID
     */
    private String orgId;

    /**
     * 组织机构路径
     */
    private String orgPath;

    /**
     * 组织机构层级
     */
    private Integer orgLevel;
}