package com.kg.module.generator.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 代码生成器表单
 *
 * @author ziro
 * @date 2023-11-22
 */
public class ZFormGeneratorExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("formId", "表单id");
        EXPORT_EXCEL_COLUMN.put("formName", "表单名称");
        EXPORT_EXCEL_COLUMN.put("formJson", "表单内容json格式");
        EXPORT_EXCEL_COLUMN.put("tableName", "表名");
        EXPORT_EXCEL_COLUMN.put("tableDecription", "表描述");
        EXPORT_EXCEL_COLUMN.put("basePackage", "pom模块名");
        EXPORT_EXCEL_COLUMN.put("author", "作者");
        EXPORT_EXCEL_COLUMN.put("tablePackage", "生成包名");
        EXPORT_EXCEL_COLUMN.put("viewPath", "前端路径");
        EXPORT_EXCEL_COLUMN.put("status", "代码生成状态（0未生成1已生成）");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "显示顺序");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("表单id", "formId");
        IMPORT_EXCEL_COLUMN.put("表单名称", "formName");
        IMPORT_EXCEL_COLUMN.put("表单内容json格式", "formJson");
        IMPORT_EXCEL_COLUMN.put("表名", "tableName");
        IMPORT_EXCEL_COLUMN.put("表描述", "tableDecription");
        IMPORT_EXCEL_COLUMN.put("pom模块名", "basePackage");
        IMPORT_EXCEL_COLUMN.put("作者", "author");
        IMPORT_EXCEL_COLUMN.put("生成包名", "tablePackage");
        IMPORT_EXCEL_COLUMN.put("前端路径", "viewPath");
        IMPORT_EXCEL_COLUMN.put("代码生成状态（0未生成1已生成）", "status");
        IMPORT_EXCEL_COLUMN.put("显示顺序", "orderIndex");
        IMPORT_EXCEL_COLUMN.put("添加时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("修改时间", "updateTime");
    }

}