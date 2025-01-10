package com.kg.component.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.math.BigDecimal;

/**
 * 字符串可转换类型检查工具类
 *
 * @author ziro
 * @date 2025/1/10 8:33
 */
public class StrTypeCheckUtils {

    /**
     * 判断是否为 数字
     */
    public static boolean isNumeric(String str) {
        return isLong(str) || isInteger(str) || isBigDecimal(str) || isDouble(str) || isFloat(str);
    }

    /**
     * 判断是否为 Integer 类型
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为 Double 类型
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为 Float 类型
     */
    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为 BigDecimal 类型
     */
    public static boolean isBigDecimal(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为 Long 类型
     */
    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为 日期
     */
    public static boolean isDateAny(String str, String pattern) {
        return isDate(str) || isLocalDate(str) || isLocalDateTime(str);
    }

    /**
     * 判断是否为 Date 类型
     */
    public static boolean isDate(String str) {
        try {
            DateUtil.parse(str);// 尝试解析日期
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否为 LocalDate 类型
     */
    public static boolean isLocalDate(String str) {
        try {
            LocalDateTimeUtil.parseDate(str);// 尝试解析日期
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否为 LocalDateTime 类型
     */
    public static boolean isLocalDateTime(String str) {
        try {
            LocalDateTimeUtil.parse(str);// 尝试解析日期
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
