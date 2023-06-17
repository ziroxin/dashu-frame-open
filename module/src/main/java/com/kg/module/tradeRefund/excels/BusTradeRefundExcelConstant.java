package com.kg.module.tradeRefund.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 退款 - 支付demo
 *
 * @author ziro
 * @date 2023-06-14
 */
public class BusTradeRefundExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("refundId", "退款ID");
        EXPORT_EXCEL_COLUMN.put("tradeId", "订单ID");
        EXPORT_EXCEL_COLUMN.put("outRefundNo", "商户退款单号");
        EXPORT_EXCEL_COLUMN.put("refundDesc", "退款原因");
        EXPORT_EXCEL_COLUMN.put("refundStatus", "退款状态0退款中1退款成功2退款异常");
        EXPORT_EXCEL_COLUMN.put("refundSuccessTime", "退款成功时间");
        EXPORT_EXCEL_COLUMN.put("refundFee", "退款金额，单位：分");
        EXPORT_EXCEL_COLUMN.put("refundResultJson", "退款反馈结果json");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("退款ID", "refundId");
        IMPORT_EXCEL_COLUMN.put("订单ID", "tradeId");
        IMPORT_EXCEL_COLUMN.put("商户退款单号", "outRefundNo");
        IMPORT_EXCEL_COLUMN.put("退款原因", "refundDesc");
        IMPORT_EXCEL_COLUMN.put("退款状态0退款中1退款成功2退款异常", "refundStatus");
        IMPORT_EXCEL_COLUMN.put("退款成功时间", "refundSuccessTime");
        IMPORT_EXCEL_COLUMN.put("退款金额，单位：分", "refundFee");
        IMPORT_EXCEL_COLUMN.put("退款反馈结果json", "refundResultJson");
        IMPORT_EXCEL_COLUMN.put("添加时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("修改时间", "updateTime");
    }

}