package com.kg.module.userTheme.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 用户主题配置
 *
 * @author ziro
 * @date 2023-11-04
 */
public class ZUserThemeExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("themeId", "用户主题配置id");
        EXPORT_EXCEL_COLUMN.put("userId", "用户id");
        EXPORT_EXCEL_COLUMN.put("themeJson", "主题内容json");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("用户主题配置id", "themeId");
        IMPORT_EXCEL_COLUMN.put("用户id", "userId");
        IMPORT_EXCEL_COLUMN.put("主题内容json", "themeJson");
        IMPORT_EXCEL_COLUMN.put("添加时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("修改时间", "updateTime");
    }

}