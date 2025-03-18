package com.kg.module.atable.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 我的表a_table
 *
 * @author ziro
 * @date 2025-03-18
 */
public class ATableExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("mobile", "手机号");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "顺序");
        EXPORT_EXCEL_COLUMN.put("testText", "测试单行文本");
        EXPORT_EXCEL_COLUMN.put("testEditor", "富文本框");
        EXPORT_EXCEL_COLUMN.put("testDecimal", "测试decimal");
        EXPORT_EXCEL_COLUMN.put("testImg", "ImageOne");
        EXPORT_EXCEL_COLUMN.put("testAvatar", "ImageAvatar");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("手机号", "mobile");
        IMPORT_EXCEL_COLUMN.put("顺序", "orderIndex");
        IMPORT_EXCEL_COLUMN.put("测试单行文本", "testText");
        IMPORT_EXCEL_COLUMN.put("富文本框", "testEditor");
        IMPORT_EXCEL_COLUMN.put("测试decimal", "testDecimal");
        IMPORT_EXCEL_COLUMN.put("ImageOne", "testImg");
        IMPORT_EXCEL_COLUMN.put("ImageAvatar", "testAvatar");
        // 初始化导入必填字段
        IMPORT_REQUIRED_COLUMN.put("mobile", "手机号");
        IMPORT_REQUIRED_COLUMN.put("orderIndex", "顺序");
    }

}