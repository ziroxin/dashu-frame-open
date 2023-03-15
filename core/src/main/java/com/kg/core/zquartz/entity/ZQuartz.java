package com.kg.core.zquartz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
@TableName("z_quartz")
@ApiModel(value = "ZQuartz对象", description = "定时任务调度表")
public class ZQuartz implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("定时任务id")
    @TableId(value = "quartz_id", type = IdType.ASSIGN_UUID)
    private String quartzId;

    @ApiModelProperty("任务名称（不能重复）")
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty("任务执行类（该类必须实现org.quartz.Job）")
    @TableField("job_class")
    private String jobClass;

    @ApiModelProperty("任务执行时间")
    @TableField("job_time_cron")
    private String jobTimeCron;

    @ApiModelProperty("任务描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("状态0关闭1开启")
    @TableField("status")
    private String status;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("最后更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
