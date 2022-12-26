package com.kg.core.zquartz.dto;

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
 * 定时任务调度表
 * </p>
 *
 * @author ziro
 * @since 2022-12-26
 */
@Getter
@Setter
@ApiModel(value = "ZQuartzDTO", description = "定时任务调度表")
public class ZQuartzDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("定时任务id")
    private String quartzId;

    @ApiModelProperty("任务名称（不能重复）")
    private String jobName;

    @ApiModelProperty("任务执行类（该类必须实现org.quartz.Job）")
    private String jobClass;

    @ApiModelProperty("任务执行时间")
    private String jobTimeCron;

    @ApiModelProperty("任务描述")
    private String description;

    @ApiModelProperty("状态0关闭1开启")
    private String status;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("最后更新时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
