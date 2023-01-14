package com.kg.core.zorg.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 下拉选择框组织机构树
 *
 * @author ziro
 * @date 2023-01-12 16:33:36
 */
@Getter
@Setter
@ApiModel(value = "ZOrganizationCascaderDTO", description = "下拉选择框组织机构树")
public class ZOrganizationCascaderDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组织机构id")
    private String value;

    @ApiModelProperty(value = "组织机构名称")
    private String label;

    @ApiModelProperty(value = "子节点")
    private List<ZOrganizationCascaderDTO> children;
}
