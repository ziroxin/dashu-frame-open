package com.kg.module.config.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 参数参数配置
 * </p>
 *
 * @author ziro
 * @since 2024-05-27
 */
@Getter
@Setter
@TableName("z_config")
@ApiModel(value = "ZConfig对象", description = "参数参数配置")
public class ZConfig implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("系统参数ID")
    @TableId(value = "cfg_id", type = IdType.ASSIGN_UUID)
    private String cfgId;

    @ApiModelProperty("参数名称")
    @TableField("cfg_name")
    private String cfgName;

    @ApiModelProperty("参数键名")
    @TableField("cfg_key")
    private String cfgKey;

    @ApiModelProperty("参数键值")
    @TableField("cfg_value")
    private String cfgValue;

    @ApiModelProperty("是否系统参数（0否1是）")
    @TableField("cfg_is_sys")
    private String cfgIsSys;

    @ApiModelProperty("备注")
    @TableField("cfg_remark")
    private String cfgRemark;

    @ApiModelProperty("顺序")
    @TableField("order_index")
    private Integer orderIndex;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
