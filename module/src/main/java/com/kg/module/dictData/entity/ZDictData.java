package com.kg.module.dictData.entity;

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
 * 字典数据
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@Getter
@Setter
@TableName("z_dict_data")
@ApiModel(value = "ZDictData对象", description = "字典数据")
public class ZDictData implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典数据ID")
    @TableId(value = "dict_id", type = IdType.ASSIGN_UUID)
    private String dictId;

    @ApiModelProperty("所属类型code")
    @TableField("type_code")
    private String typeCode;

    @ApiModelProperty("字典标签")
    @TableField("dict_label")
    private String dictLabel;

    @ApiModelProperty("字典值")
    @TableField("dict_value")
    private String dictValue;

    @ApiModelProperty("状态0停用1正常")
    @TableField("status")
    private String status;

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
