package com.kg.module.userTheme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


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
@ApiModel(value = "ZUserThemeDTO", description = "用户主题配置")
public class ZUserThemeDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户主题配置id")
    private String themeId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("主题内容json")
    private String themeJson;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
