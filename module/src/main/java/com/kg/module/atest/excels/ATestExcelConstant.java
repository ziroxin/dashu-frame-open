package com.kg.module.atest.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 测试表
 *
 * @author ziro
 * @date 2023-03-30
 */
public class ATestExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("testId", "测试id");
        EXPORT_EXCEL_COLUMN.put("testName", "姓名");
        EXPORT_EXCEL_COLUMN.put("testAge", "年龄");
        EXPORT_EXCEL_COLUMN.put("testSex", "性别");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "顺序");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("测试id", "testId");
        IMPORT_EXCEL_COLUMN.put("姓名", "testName");
        IMPORT_EXCEL_COLUMN.put("年龄", "testAge");
        IMPORT_EXCEL_COLUMN.put("性别", "testSex");
        IMPORT_EXCEL_COLUMN.put("顺序", "orderIndex");
        IMPORT_EXCEL_COLUMN.put("添加时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("修改时间", "updateTime");
    }

}