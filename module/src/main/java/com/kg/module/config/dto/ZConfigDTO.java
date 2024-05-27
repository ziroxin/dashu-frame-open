package com.kg.module.config.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
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
@ApiModel(value = "ZConfigDTO", description = "参数参数配置")
public class ZConfigDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("系统参数ID")
    private String cfgId;

    @ApiModelProperty("参数名称")
    private String cfgName;

    @ApiModelProperty("参数键名")
    private String cfgKey;

    @ApiModelProperty("参数键值")
    private String cfgValue;

    @ApiModelProperty("是否系统参数（0否1是）")
    private String cfgIsSys;

    @ApiModelProperty("备注")
    private String cfgRemark;

    @ApiModelProperty("顺序")
    private Integer orderIndex;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
