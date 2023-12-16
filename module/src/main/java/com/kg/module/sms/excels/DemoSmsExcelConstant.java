package com.kg.module.sms.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 短信 - demo
 *
 * @author ziro
 * @date 2023-12-14
 */
public class DemoSmsExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("smsId", "发送短信id");
        EXPORT_EXCEL_COLUMN.put("smsChannel", "发送渠道（例如：阿里云短信）");
        EXPORT_EXCEL_COLUMN.put("smsPhones", "手机号（多个以英文逗号隔开，最多支持1000个）");
        EXPORT_EXCEL_COLUMN.put("sendJson", "发送短信内容json");
        EXPORT_EXCEL_COLUMN.put("status", "发送状态（0发送失败1发送成功）");
        EXPORT_EXCEL_COLUMN.put("resultJson", "返回结果json");
        EXPORT_EXCEL_COLUMN.put("createTime", "发送时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("发送短信id", "smsId");
        IMPORT_EXCEL_COLUMN.put("发送渠道（例如：阿里云短信）", "smsChannel");
        IMPORT_EXCEL_COLUMN.put("手机号（多个以英文逗号隔开，最多支持1000个）", "smsPhones");
        IMPORT_EXCEL_COLUMN.put("发送短信内容json", "sendJson");
        IMPORT_EXCEL_COLUMN.put("发送状态（0发送失败1发送成功）", "status");
        IMPORT_EXCEL_COLUMN.put("返回结果json", "resultJson");
        IMPORT_EXCEL_COLUMN.put("发送时间", "createTime");
    }

}