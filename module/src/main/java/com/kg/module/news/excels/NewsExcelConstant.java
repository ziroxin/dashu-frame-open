package com.kg.module.news.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 新闻表-测试
 *
 * @author ziro
 * @date 2023-02-17
 */
public class NewsExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("newsId", "新闻id");
        EXPORT_EXCEL_COLUMN.put("newsTitle", "新闻标题");
        EXPORT_EXCEL_COLUMN.put("newsContent", "新闻内容");
        EXPORT_EXCEL_COLUMN.put("orderIndex", "顺序");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
    }

}