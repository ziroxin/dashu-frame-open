package com.kg.module.atable.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 我的表a_table
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@Getter
@Setter
@TableName("a_table")
@ApiModel(value = "ATable对象", description = "我的表a_table")
public class ATable implements BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty("手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty("顺序")
    @TableField("order_index")
    private Integer orderIndex;

    @ApiModelProperty("测试单行文本")
    @TableField("test_text")
    private String testText;

    @ApiModelProperty("富文本框")
    @TableField("test_editor")
    private String testEditor;

    @ApiModelProperty("测试decimal")
    @TableField("test_decimal")
    private BigDecimal testDecimal;

    @ApiModelProperty("ImageOne")
    @TableField("test_img")
    private String testImg;

    @ApiModelProperty("ImageAvatar")
    @TableField("test_avatar")
    private String testAvatar;

    @ApiModelProperty("添加时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
