package com.kg.core.zuserlock.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 用户锁定
 *
 * @author ziro
 * @date 2023-01-05
 */
public class ZUserLockExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("lockId", "用户锁定id");
        EXPORT_EXCEL_COLUMN.put("userName", "用户名");
        EXPORT_EXCEL_COLUMN.put("lockReason", "锁定原因");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
    }

}