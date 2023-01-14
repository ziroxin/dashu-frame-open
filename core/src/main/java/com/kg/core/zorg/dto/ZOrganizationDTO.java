package com.kg.core.zorg.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 组织机构表
 * </p>
 *
 * @author ziro
 * @since 2023-01-11
 */
@Getter
@Setter
@ApiModel(value = "ZOrganizationDTO", description = "组织机构表")
public class ZOrganizationDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("组织机构ID")
    private String orgId;

    @ApiModelProperty("组织机构名称")
    private String orgName;

    @ApiModelProperty("父级ID")
    private String orgParentId;

    @ApiModelProperty("组织机构路径(格式:id1.id2.id3)")
    private String orgPath;

    @ApiModelProperty("层级")
    private Integer orgLevel;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("顺序")
    private Integer orderIndex;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty("子节点内容")
    private List<ZOrganizationDTO> children;
}
