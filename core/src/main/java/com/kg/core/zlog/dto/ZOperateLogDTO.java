package com.kg.core.zlog.dto;

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
 * 操作日志表
 * </p>
 *
 * @author ziro
 * @since 2023-01-07
 */
@Getter
@Setter
@ApiModel(value = "ZOperateLogDTO", description = "操作日志表")
public class ZOperateLogDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("操作日志id")
    private String logId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("操作人用户名")
    private String userName;

    @ApiModelProperty("执行方法名称")
    private String logMethod;

    @ApiModelProperty("执行方法描述")
    private String logMsg;

    @ApiModelProperty("操作内容")
    private String content;

    @ApiModelProperty("请求路径")
    private String actionUrl;

    @ApiModelProperty("IP地址")
    private String ip;

    @ApiModelProperty("执行结果（1成功 2失败）")
    private Byte status;

    @ApiModelProperty("操作时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
