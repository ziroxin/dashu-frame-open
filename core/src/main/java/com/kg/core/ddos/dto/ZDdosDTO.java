package com.kg.core.ddos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * ddos用户请求记录
 * </p>
 *
 * @author ziro
 * @since 2024-07-15
 */
@Getter
@Setter
@ApiModel(value = "ZDdosDTO", description = "ddos用户请求记录")
public class ZDdosDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("DDoS_id")
    private String ddosId;

    @ApiModelProperty("用户ID")
    private String userIp;

    @ApiModelProperty("请求次数")
    private Integer requestCount;

    @ApiModelProperty("限制配置（例如：5分钟内限制100次）")
    private String limitJson;

    @ApiModelProperty("用户id（有则保存）")
    private String userId;

    @ApiModelProperty("记录时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
