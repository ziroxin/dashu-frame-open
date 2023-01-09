package com.kg.core.zlog.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 操作日志表
 *
 * @author ziro
 * @date 2023-01-07
 */
public class ZOperateLogExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("logId", "操作日志id");
        EXPORT_EXCEL_COLUMN.put("userId", "用户id");
        EXPORT_EXCEL_COLUMN.put("userName", "操作人用户名");
        EXPORT_EXCEL_COLUMN.put("logMethod", "执行方法名称");
        EXPORT_EXCEL_COLUMN.put("logMsg", "执行方法描述");
        EXPORT_EXCEL_COLUMN.put("content", "操作内容");
        EXPORT_EXCEL_COLUMN.put("actionUrl", "请求路径");
        EXPORT_EXCEL_COLUMN.put("ip", "IP地址");
        EXPORT_EXCEL_COLUMN.put("createTime", "操作时间");
    }

}