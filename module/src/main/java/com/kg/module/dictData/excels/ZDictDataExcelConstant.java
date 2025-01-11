package com.kg.module.dictData.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 字典数据
 *
 * @author ziro
 * @date 2023-05-26
 */
public class ZDictDataExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();
    /**
     * 导入所需字段信息
     */
    public static LinkedHashMap<String, String> IMPORT_EXCEL_COLUMN = new LinkedHashMap<>();
    /**
     * 导入必填字段信息
     */
    public static LinkedHashMap<String, String> IMPORT_REQUIRED_COLUMN = new LinkedHashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("dictLabel", "数据标签");
        EXPORT_EXCEL_COLUMN.put("dictValue", "数据值");
        EXPORT_EXCEL_COLUMN.put("status", "状态");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "顺序");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("数据标签", "dictLabel");
        IMPORT_EXCEL_COLUMN.put("数据值", "dictValue");
        IMPORT_EXCEL_COLUMN.put("顺序", "orderIndex");
        // 初始化导入必填字段
        IMPORT_REQUIRED_COLUMN.put("dictLabel", "数据标签");
        IMPORT_REQUIRED_COLUMN.put("dictValue", "数据值");
        IMPORT_REQUIRED_COLUMN.put("orderIndex", "顺序");
    }

}