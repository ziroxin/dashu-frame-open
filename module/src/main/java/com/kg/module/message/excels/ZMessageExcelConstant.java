package com.kg.module.message.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 消息中心
 *
 * @author ziro
 * @date 2024-06-18
 */
public class ZMessageExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("msgId", "消息中心id");
        EXPORT_EXCEL_COLUMN.put("msgTitle", "消息标题");
        EXPORT_EXCEL_COLUMN.put("msgContent", "消息内容");
        EXPORT_EXCEL_COLUMN.put("msgRouter", "跳转路由");
        EXPORT_EXCEL_COLUMN.put("permissionName", "所属模块/菜单（可为空）");
        EXPORT_EXCEL_COLUMN.put("createTime", "消息创建时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("消息中心id", "msgId");
        IMPORT_EXCEL_COLUMN.put("消息标题", "msgTitle");
        IMPORT_EXCEL_COLUMN.put("消息内容", "msgContent");
        IMPORT_EXCEL_COLUMN.put("跳转路由", "msgRouter");
        IMPORT_EXCEL_COLUMN.put("所属模块/菜单（可为空）", "permissionName");
        IMPORT_EXCEL_COLUMN.put("消息创建时间", "createTime");
    }

}