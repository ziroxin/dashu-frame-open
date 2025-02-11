package com.kg.module.atable.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.kg.core.base.dto.BaseDTO;
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
 * @since 2025-02-11
 */
@Getter
@Setter
@ApiModel(value = "ATableDTO", description = "我的表a_table")
public class ATableDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("顺序")
    private Integer orderIndex;

    @ApiModelProperty("测试单行文本")
    private String field116;

    @ApiModelProperty("测试decimal")
    private BigDecimal a;

    @ApiModelProperty("测试富文本")
    private String b;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
