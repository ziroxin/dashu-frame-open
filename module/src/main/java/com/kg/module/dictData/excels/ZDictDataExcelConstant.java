package com.kg.module.dictData.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public static Map<String, String> IMPORT_EXCEL_COLUMN = new HashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("dictId", "字典数据ID");
        EXPORT_EXCEL_COLUMN.put("typeCode", "所属类型code");
        EXPORT_EXCEL_COLUMN.put("dictLabel", "字典标签");
        EXPORT_EXCEL_COLUMN.put("dictValue", "字典值");
        EXPORT_EXCEL_COLUMN.put("status", "状态0停用1正常");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "顺序");
        EXPORT_EXCEL_COLUMN.put("createTime", "创建时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("字典数据ID", "dictId");
        IMPORT_EXCEL_COLUMN.put("所属类型code", "typeCode");
        IMPORT_EXCEL_COLUMN.put("字典标签", "dictLabel");
        IMPORT_EXCEL_COLUMN.put("字典值", "dictValue");
        IMPORT_EXCEL_COLUMN.put("状态0停用1正常", "status");
        IMPORT_EXCEL_COLUMN.put("顺序", "orderIndex");
        IMPORT_EXCEL_COLUMN.put("创建时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("修改时间", "updateTime");
    }

}