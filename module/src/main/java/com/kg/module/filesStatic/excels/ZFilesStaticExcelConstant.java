package com.kg.module.filesStatic.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 静态资源文件表
 *
 * @author ziro
 * @date 2025-01-24
 */
public class ZFilesStaticExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("parentId", "所属文件夹id");
        EXPORT_EXCEL_COLUMN.put("fileMd5", "文件md5");
        EXPORT_EXCEL_COLUMN.put("fileUrl", "文件地址（文件访问地址）");
        EXPORT_EXCEL_COLUMN.put("fileOldName", "原文件名");
        EXPORT_EXCEL_COLUMN.put("fileName", "存储文件名");
        EXPORT_EXCEL_COLUMN.put("fileExtend", "文件扩展名");
        EXPORT_EXCEL_COLUMN.put("fileSize", "文件大小");
        EXPORT_EXCEL_COLUMN.put("fileType", "文件类型(0文件夹1文件)");
        EXPORT_EXCEL_COLUMN.put("createTime", "创建时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("所属文件夹id", "parentId");
        IMPORT_EXCEL_COLUMN.put("文件md5", "fileMd5");
        IMPORT_EXCEL_COLUMN.put("文件地址（文件访问地址）", "fileUrl");
        IMPORT_EXCEL_COLUMN.put("原文件名", "fileOldName");
        IMPORT_EXCEL_COLUMN.put("存储文件名", "fileName");
        IMPORT_EXCEL_COLUMN.put("文件扩展名", "fileExtend");
        IMPORT_EXCEL_COLUMN.put("文件大小", "fileSize");
        IMPORT_EXCEL_COLUMN.put("文件类型(0文件夹1文件)", "fileType");
        // 初始化导入必填字段
        IMPORT_REQUIRED_COLUMN.put("fileType", "文件类型(0文件夹1文件)");
    }

}