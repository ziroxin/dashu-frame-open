package com.kg.core.zquartz.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 导出Excel实体 - 定时任务调度表
 *
 * @author ziro
 * @date 2022-12-28 09:01:49
 */
@Getter
@Setter
public class ZQuartzExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 定时任务id
     */
    private String quartzId;

    /**
     * 任务名称（不能重复）
     */
    private String jobName;

    /**
     * 任务执行类（该类必须实现org.quartz.Job）
     */
    private String jobClass;

    /**
     * 任务执行时间
     */
    private String jobTimeCron;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 状态0关闭1开启
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
