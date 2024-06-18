package com.kg.module.messageTo.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 消息发送至
 *
 * @author ziro
 * @date 2024-06-18
 */
public class ZMessageToExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("toId", "消息发送至ID");
        EXPORT_EXCEL_COLUMN.put("msgId", "消息ID");
        EXPORT_EXCEL_COLUMN.put("toUserId", "收信用户ID");
        EXPORT_EXCEL_COLUMN.put("sendUserId", "发信用户ID");
        EXPORT_EXCEL_COLUMN.put("msgStatus", "消息状态（0未读1已读）");
        EXPORT_EXCEL_COLUMN.put("readTime", "已读时间");
        EXPORT_EXCEL_COLUMN.put("createTime", "创建时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("消息发送至ID", "toId");
        IMPORT_EXCEL_COLUMN.put("消息ID", "msgId");
        IMPORT_EXCEL_COLUMN.put("收信用户ID", "toUserId");
        IMPORT_EXCEL_COLUMN.put("发信用户ID", "sendUserId");
        IMPORT_EXCEL_COLUMN.put("消息状态（0未读1已读）", "msgStatus");
        IMPORT_EXCEL_COLUMN.put("已读时间", "readTime");
        IMPORT_EXCEL_COLUMN.put("创建时间", "createTime");
    }

}