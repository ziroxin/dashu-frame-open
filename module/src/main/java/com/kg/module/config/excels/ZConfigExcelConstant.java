package com.kg.module.config.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 参数参数配置
 *
 * @author ziro
 * @date 2024-05-27
 */
public class ZConfigExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("cfgId", "系统参数ID");
        EXPORT_EXCEL_COLUMN.put("cfgName", "参数名称");
        EXPORT_EXCEL_COLUMN.put("cfgKey", "参数键名");
        EXPORT_EXCEL_COLUMN.put("cfgValue", "参数键值");
        EXPORT_EXCEL_COLUMN.put("cfgRemark", "备注");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "顺序");
        EXPORT_EXCEL_COLUMN.put("createTime", "创建时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("系统参数ID", "cfgId");
        IMPORT_EXCEL_COLUMN.put("参数名称", "cfgName");
        IMPORT_EXCEL_COLUMN.put("参数键名", "cfgKey");
        IMPORT_EXCEL_COLUMN.put("参数键值", "cfgValue");
        IMPORT_EXCEL_COLUMN.put("备注", "cfgRemark");
        IMPORT_EXCEL_COLUMN.put("顺序", "orderIndex");
        IMPORT_EXCEL_COLUMN.put("创建时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("修改时间", "updateTime");
    }

}