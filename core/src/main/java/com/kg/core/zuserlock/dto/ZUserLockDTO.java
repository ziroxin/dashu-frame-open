package com.kg.core.zuserlock.dto;

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
 * 用户锁定
 * </p>
 *
 * @author ziro
 * @since 2023-01-05
 */
@Getter
@Setter
@ApiModel(value = "ZUserLockDTO", description = "用户锁定")
public class ZUserLockDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户锁定id")
    private String lockId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("锁定原因")
    private String lockReason;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
