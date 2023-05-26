package com.kg.module.dictType.entity;

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
 * 字典类型
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@Getter
@Setter
@TableName("z_dict_type")
@ApiModel(value = "ZDictType对象", description = "字典类型")
public class ZDictType implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典类型ID")
    @TableId(value = "type_id", type = IdType.ASSIGN_UUID)
    private String typeId;

    @ApiModelProperty("字典名称")
    @TableField("type_name")
    private String typeName;

    @ApiModelProperty("字典类型code")
    @TableField("type_code")
    private String typeCode;

    @ApiModelProperty("状态0停用1正常")
    @TableField("status")
    private String status;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
