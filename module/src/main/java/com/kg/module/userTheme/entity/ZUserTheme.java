package com.kg.module.userTheme.entity;

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
 * 用户主题配置
 * </p>
 *
 * @author ziro
 * @since 2023-11-04
 */
@Getter
@Setter
@TableName("z_user_theme")
@ApiModel(value = "ZUserTheme对象", description = "用户主题配置")
public class ZUserTheme implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户主题配置id")
    @TableId(value = "theme_id", type = IdType.ASSIGN_UUID)
    private String themeId;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("主题内容json")
    @TableField("theme_json")
    private String themeJson;

    @ApiModelProperty("添加时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
