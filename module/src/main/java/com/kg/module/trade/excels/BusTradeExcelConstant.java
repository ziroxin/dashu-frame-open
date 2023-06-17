package com.kg.module.trade.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - 交易 - 支付demo
 *
 * @author ziro
 * @date 2023-05-16
 */
public class BusTradeExcelConstant {
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
        EXPORT_EXCEL_COLUMN.put("tradeId", "订单ID");
        EXPORT_EXCEL_COLUMN.put("productId", "商品ID（外链到需要付款的商品或其他信息）");
        EXPORT_EXCEL_COLUMN.put("userId", "用户id（z_user表）");
        EXPORT_EXCEL_COLUMN.put("payType", "支付方式0微信1支付宝");
        EXPORT_EXCEL_COLUMN.put("tradeOpenId", "支付用户微信/支付宝id");
        EXPORT_EXCEL_COLUMN.put("tradeStatus", "支付状态0未支付1已支付");
        EXPORT_EXCEL_COLUMN.put("paySuccessTime", "支付成功时间");
        EXPORT_EXCEL_COLUMN.put("totalFee", "总金额，单位：分");
        EXPORT_EXCEL_COLUMN.put("outTradeNo", "商户订单号（微信32支付宝64内）");
        EXPORT_EXCEL_COLUMN.put("attach", "附加内容（在查询API和支付通知中原样返回）");
        EXPORT_EXCEL_COLUMN.put("resultJson", "支付反馈结果json");
        EXPORT_EXCEL_COLUMN.put("refundTotalFee", "已退款总金额，单位：分");
        EXPORT_EXCEL_COLUMN.put("createTime", "添加时间");
        EXPORT_EXCEL_COLUMN.put("updateTime", "修改时间");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("订单ID", "tradeId");
        IMPORT_EXCEL_COLUMN.put("商品ID（外链到需要付款的商品或其他信息）", "productId");
        IMPORT_EXCEL_COLUMN.put("用户id（z_user表）", "userId");
        IMPORT_EXCEL_COLUMN.put("支付方式0微信1支付宝", "payType");
        IMPORT_EXCEL_COLUMN.put("支付用户微信/支付宝id", "tradeOpenId");
        IMPORT_EXCEL_COLUMN.put("支付状态0未支付1已支付", "tradeStatus");
        IMPORT_EXCEL_COLUMN.put("支付成功时间", "paySuccessTime");
        IMPORT_EXCEL_COLUMN.put("总金额，单位：分", "totalFee");
        IMPORT_EXCEL_COLUMN.put("商户订单号（微信32支付宝64内）", "outTradeNo");
        IMPORT_EXCEL_COLUMN.put("附加内容（在查询API和支付通知中原样返回）", "attach");
        IMPORT_EXCEL_COLUMN.put("支付反馈结果json", "resultJson");
        EXPORT_EXCEL_COLUMN.put("已退款总金额，单位：分", "refundTotalFee");
        IMPORT_EXCEL_COLUMN.put("添加时间", "createTime");
        IMPORT_EXCEL_COLUMN.put("修改时间", "updateTime");
    }

}