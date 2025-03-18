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
        EXPORT_EXCEL_COLUMN.put("testName", "姓名");
        EXPORT_EXCEL_COLUMN.put("field118", "ImageAvatar");
        EXPORT_EXCEL_COLUMN.put("field119", "ImageOne");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("姓名", "testName");
        IMPORT_EXCEL_COLUMN.put("ImageAvatar", "field118");
        IMPORT_EXCEL_COLUMN.put("ImageOne", "field119");
        // 初始化导入必填字段
    }

}