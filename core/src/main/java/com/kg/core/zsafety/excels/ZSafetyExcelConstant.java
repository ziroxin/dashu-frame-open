package com.kg.core.zsafety.excels;

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - 密码安全等设置
 *
 * @author ziro
 * @date 2022-12-30
 */
public class ZSafetyExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("id", "配置ID");
        EXPORT_EXCEL_COLUMN.put("startLength", "开始长度");
        EXPORT_EXCEL_COLUMN.put("endLength", "结束长度");
        EXPORT_EXCEL_COLUMN.put("lowercase", "小写字母 0必须无 1必须有 2可有可无");
        EXPORT_EXCEL_COLUMN.put("uppercase", "大写字母 0必须无 1必须有 2可有可无");
        EXPORT_EXCEL_COLUMN.put("numbers", "数字 0必须无 1必须有 2可有可无");
        EXPORT_EXCEL_COLUMN.put("specialCharacters", "是否有特殊字符 0必须无 1必须有 2可有可无");
        EXPORT_EXCEL_COLUMN.put("banUsername", "不能包含用户名 0否 1是");
        EXPORT_EXCEL_COLUMN.put("validTime", "有效时间 天");
        EXPORT_EXCEL_COLUMN.put("prompt", "提示语");
        EXPORT_EXCEL_COLUMN.put("loginFailedTimes", "登录失败限制次数");
        EXPORT_EXCEL_COLUMN.put("lockTime", "锁定时间 分钟");
        EXPORT_EXCEL_COLUMN.put("defaultPassword", "默认密码");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
    }

}