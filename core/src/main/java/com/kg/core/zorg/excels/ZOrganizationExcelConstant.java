package com.kg.core.zorg.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 组织机构表
 *
 * @author ziro
 * @date 2023-01-11
 */
public class ZOrganizationExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("orgId", "组织机构ID");
        EXPORT_EXCEL_COLUMN.put("orgName", "组织机构名称");
        EXPORT_EXCEL_COLUMN.put("orgParentId", "父级ID");
        EXPORT_EXCEL_COLUMN.put("orgPath", "组织机构路径(格式:id1.id2.id3)");
        EXPORT_EXCEL_COLUMN.put("orgLevel", "层级");
        EXPORT_EXCEL_COLUMN.put("remarks", "备注");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "顺序");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("组织机构名称", "orgName");
        IMPORT_EXCEL_COLUMN.put("上级部门", "orgParentId");
        IMPORT_EXCEL_COLUMN.put("顺序", "orderIndex");
        IMPORT_EXCEL_COLUMN.put("备注", "remarks");
        // 初始化导入必填字段
        IMPORT_REQUIRED_COLUMN.put("orgName", "组织机构名称");
        IMPORT_REQUIRED_COLUMN.put("orgParentId", "上级部门");
        IMPORT_REQUIRED_COLUMN.put("orderIndex", "顺序");
    }

}