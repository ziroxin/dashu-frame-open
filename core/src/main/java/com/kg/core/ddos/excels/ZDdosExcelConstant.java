package com.kg.core.ddos.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - ddos用户请求记录
 *
 * @author ziro
 * @date 2024-07-15
 */
public class ZDdosExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();
    /**
     * 导入所需字段信息
     */
    public static Map<String, String> IMPORT_EXCEL_COLUMN = new HashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("ddosId", "DDoS_id");
        EXPORT_EXCEL_COLUMN.put("userIp", "用户ID");
        EXPORT_EXCEL_COLUMN.put("requestCount", "请求次数");
        EXPORT_EXCEL_COLUMN.put("limitJson", "限制配置（例如：5分钟内限制100次）");
        EXPORT_EXCEL_COLUMN.put("userId", "用户id（有则保存）");
        EXPORT_EXCEL_COLUMN.put("createTime", "记录时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("DDoS_id", "ddosId");
        IMPORT_EXCEL_COLUMN.put("用户ID", "userIp");
        IMPORT_EXCEL_COLUMN.put("请求次数", "requestCount");
        IMPORT_EXCEL_COLUMN.put("限制配置（例如：5分钟内限制100次）", "limitJson");
        IMPORT_EXCEL_COLUMN.put("用户id（有则保存）", "userId");
        IMPORT_EXCEL_COLUMN.put("记录时间", "createTime");
    }

}