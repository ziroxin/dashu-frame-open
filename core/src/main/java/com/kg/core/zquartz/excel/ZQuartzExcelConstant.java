package com.kg.core.zquartz.excel;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 定时任务调度表
 *
 * @author ziro
 * @date 2022-12-28 09:11:17
 */
public class ZQuartzExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("quartzId", "定时任务id");
        EXPORT_EXCEL_COLUMN.put("jobName", "任务名称（不能重复）");
        EXPORT_EXCEL_COLUMN.put("jobClass", "任务执行类（该类必须实现org.quartz.Job）");
        EXPORT_EXCEL_COLUMN.put("jobTimeCron", "任务执行时间");
        EXPORT_EXCEL_COLUMN.put("description", "任务描述");
        EXPORT_EXCEL_COLUMN.put("status", "状态0关闭1开启");
        EXPORT_EXCEL_COLUMN.put("createTime", "创建时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "最后更新时间");
    }

}
