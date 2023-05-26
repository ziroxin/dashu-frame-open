package com.kg.module.dictType.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 字典类型
 *
 * @author ziro
 * @date 2023-05-26
 */
public class ZDictTypeExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("typeId", "字典类型ID");
        EXPORT_EXCEL_COLUMN.put("typeName", "字典名称");
        EXPORT_EXCEL_COLUMN.put("typeCode", "字典类型code");
        EXPORT_EXCEL_COLUMN.put("status", "状态0停用1正常");
        EXPORT_EXCEL_COLUMN.put("createTime", "创建时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "更新时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("字典类型ID", "typeId");
        IMPORT_EXCEL_COLUMN.put("字典名称", "typeName");
        IMPORT_EXCEL_COLUMN.put("字典类型code", "typeCode");
        IMPORT_EXCEL_COLUMN.put("状态0停用1正常", "status");
        IMPORT_EXCEL_COLUMN.put("创建时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("更新时间", "updateTime");
    }

}