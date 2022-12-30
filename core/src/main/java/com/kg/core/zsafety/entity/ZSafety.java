package com.kg.core.zsafety.entity;

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
 * 密码安全等设置
 * </p>
 *
 * @author ziro
 * @since 2022-12-30
 */
@Getter
@Setter
@TableName("z_safety")
@ApiModel(value = "ZSafety对象", description = "密码安全等设置")
public class ZSafety implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("配置ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    @ApiModelProperty("开始长度")
    @TableField("start_length")
    private Short startLength;

    @ApiModelProperty("结束长度")
    @TableField("end_length")
    private Short endLength;

    @ApiModelProperty("小写字母 0必须无 1必须有 2可有可无")
    @TableField("lowercase")
    private Short lowercase;

    @ApiModelProperty("大写字母 0必须无 1必须有 2可有可无")
    @TableField("uppercase")
    private Short uppercase;

    @ApiModelProperty("数字 0必须无 1必须有 2可有可无")
    @TableField("numbers")
    private Short numbers;

    @ApiModelProperty("是否有特殊字符 0必须无 1必须有 2可有可无")
    @TableField("special_characters")
    private Short specialCharacters;

    @ApiModelProperty("不能包含用户名 0否 1是")
    @TableField("ban_username")
    private Short banUsername;

    @ApiModelProperty("有效时间 天")
    @TableField("valid_time")
    private Short validTime;

    @ApiModelProperty("提示语")
    @TableField("prompt")
    private String prompt;

    @ApiModelProperty("登录失败限制次数")
    @TableField("login_failed_times")
    private Short loginFailedTimes;

    @ApiModelProperty("锁定时间 分钟")
    @TableField("lock_time")
    private Short lockTime;

    @ApiModelProperty("默认密码")
    @TableField("default_password")
    private String defaultPassword;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
