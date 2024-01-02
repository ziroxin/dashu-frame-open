package com.kg.module.files.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 文件记录表
 *
 * @author ziro
 * @date 2023-09-15
 */
public class ZFilesExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("fileId", "文件id");
        EXPORT_EXCEL_COLUMN.put("fileMd5", "文件md5");
        EXPORT_EXCEL_COLUMN.put("fileUrl", "文件地址（文件访问地址）");
        EXPORT_EXCEL_COLUMN.put("fileOldName", "原文件名");
        EXPORT_EXCEL_COLUMN.put("fileName", "存储文件名");
        EXPORT_EXCEL_COLUMN.put("fileExtend", "文件扩展名");
        EXPORT_EXCEL_COLUMN.put("fileSize", "文件大小");
        EXPORT_EXCEL_COLUMN.put("createTime", "创建时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("文件id", "fileId");
        IMPORT_EXCEL_COLUMN.put("文件md5", "fileMd5");
        IMPORT_EXCEL_COLUMN.put("文件地址（文件访问地址）", "fileUrl");
        IMPORT_EXCEL_COLUMN.put("原文件名", "fileOldName");
        IMPORT_EXCEL_COLUMN.put("存储文件名", "fileName");
        IMPORT_EXCEL_COLUMN.put("文件扩展名", "fileExtend");
        IMPORT_EXCEL_COLUMN.put("文件大小", "fileSize");
        IMPORT_EXCEL_COLUMN.put("创建时间", "createTime");
    }

}