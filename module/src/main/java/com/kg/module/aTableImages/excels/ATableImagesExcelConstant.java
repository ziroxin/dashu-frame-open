package com.kg.module.aTableImages.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 我的表a_table附件表
 *
 * @author ziro
 * @date 2025-03-18
 */
public class ATableImagesExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("aTableId", "主表id");
        EXPORT_EXCEL_COLUMN.put("fileUrl", "文件地址（文件访问地址）");
        EXPORT_EXCEL_COLUMN.put("fileOldName", "原文件名");
        EXPORT_EXCEL_COLUMN.put("fileName", "存储文件名");
        EXPORT_EXCEL_COLUMN.put("fileExtend", "文件扩展名");
        EXPORT_EXCEL_COLUMN.put("fileSize", "文件大小");
        EXPORT_EXCEL_COLUMN.put("createTime", "附件上传时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("主表id", "aTableId");
        IMPORT_EXCEL_COLUMN.put("文件地址（文件访问地址）", "fileUrl");
        IMPORT_EXCEL_COLUMN.put("原文件名", "fileOldName");
        IMPORT_EXCEL_COLUMN.put("存储文件名", "fileName");
        IMPORT_EXCEL_COLUMN.put("文件扩展名", "fileExtend");
        IMPORT_EXCEL_COLUMN.put("文件大小", "fileSize");
        // 初始化导入必填字段
    }

}