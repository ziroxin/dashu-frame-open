package com.kg.core.zsafety.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
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
@ApiModel(value = "ZSafetyDTO", description = "密码安全等设置")
public class ZSafetyDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("配置ID")
    private Integer id;

    @ApiModelProperty("开始长度")
    private Short startLength;

    @ApiModelProperty("结束长度")
    private Short endLength;

    @ApiModelProperty("小写字母 0必须无 1必须有 2可有可无")
    private Short lowercase;

    @ApiModelProperty("大写字母 0必须无 1必须有 2可有可无")
    private Short uppercase;

    @ApiModelProperty("数字 0必须无 1必须有 2可有可无")
    private Short numbers;

    @ApiModelProperty("是否有特殊字符 0必须无 1必须有 2可有可无")
    private Short specialCharacters;

    @ApiModelProperty("不能包含用户名 0否 1是")
    private Short banUsername;

    @ApiModelProperty("有效时间 天")
    private Short validTime;

    @ApiModelProperty("提示语")
    private String prompt;

    @ApiModelProperty("登录失败限制次数")
    private Short loginFailedTimes;

    @ApiModelProperty("锁定时间 分钟")
    private Short lockTime;

    @ApiModelProperty("默认密码")
    private String defaultPassword;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
