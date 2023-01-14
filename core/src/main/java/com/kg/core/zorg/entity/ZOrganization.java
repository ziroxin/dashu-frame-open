package com.kg.core.zorg.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

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
@TableName("z_organization")
@ApiModel(value = "ZOrganization对象", description = "组织机构表")
public class ZOrganization implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("组织机构ID")
    @TableId(value = "org_id", type = IdType.ASSIGN_UUID)
    private String orgId;

    @ApiModelProperty("组织机构名称")
    @TableField("org_name")
    private String orgName;

    @ApiModelProperty("父级ID")
    @TableField("org_parent_id")
    private String orgParentId;

    @ApiModelProperty("组织机构路径(格式:id1.id2.id3)")
    @TableField("org_path")
    private String orgPath;

    @ApiModelProperty("层级")
    @TableField("org_level")
    private Integer orgLevel;

    @ApiModelProperty("备注")
    @TableField("remarks")
    private String remarks;

    @ApiModelProperty("顺序")
    @TableField("order_index")
    private Integer orderIndex;

    @ApiModelProperty("添加时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
