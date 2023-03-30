package com.kg.module.atest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * <p>
 * 测试表
 * </p>
 *
 * @author ziro
 * @since 2023-03-30
 */
@Getter
@Setter
@ApiModel(value = "ATestDTO", description = "测试表")
public class ATestDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("测试id")
    private String testId;

    @ApiModelProperty("姓名")
    private String testName;

    @ApiModelProperty("年龄")
    private Integer testAge;

    @ApiModelProperty("性别")
    private String testSex;

    @ApiModelProperty("顺序")
    private Integer orderIndex;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
