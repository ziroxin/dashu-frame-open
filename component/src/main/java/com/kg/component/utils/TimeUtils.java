package com.kg.component.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间处理工具封装
 *
 * @author ziro
 * @date 2023-01-17 09:23:26
 */
@NoArgsConstructor
@AllArgsConstructor
public class TimeUtils {
    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 设置要操作的时间
     *
     * @param timeStamp 时间戳
     */
    public static TimeUtils setTime(long timeStamp) {
        return new TimeUtils(timeStamp);
    }

    /**
     * 设置要操作的时间
     *
     * @param date 日期时间
     */
    public static TimeUtils setTime(Date date) {
        return new TimeUtils(date.getTime());
    }

    /**
     * 设置要操作的时间
     *
     * @param localDate 时间
     */
    public static TimeUtils setTime(LocalDate localDate) {
        return setTime(localDate.atStartOfDay());
    }

    /**
     * 设置要操作的时间
     *
     * @param localDateTime 时间
     */
    public static TimeUtils setTime(LocalDateTime localDateTime) {
        return new TimeUtils(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    /**
     * 设置要操作的时间（默认字符串格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateStr 时间字符串
     */
    public static TimeUtils setTime(String dateStr) {
        return setTime(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 设置要操作的时间
     *
     * @param dateStr   时间字符串
     * @param formatStr 格式（如：yyyy-MM-dd HH:mm:ss 或者 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'）
     */
    public static TimeUtils setTime(String dateStr, String formatStr) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            return new TimeUtils(formatter.parse(dateStr).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 当前时间
     */
    public static TimeUtils now() {
        return new TimeUtils(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * 当天开始时间，格式：yyyy/MM/dd 00:00:00
     */
    public TimeUtils startOfDay() {
        return TimeUtils.setTime(TimeUtils.setTime(this.timestamp).toFormat("yyyy-MM-dd 00:00:00"));
    }

    /**
     * 当天结束时间，格式：yyyy/MM/dd 23:59:59
     */
    public TimeUtils endOfDay() {
        return TimeUtils.setTime(TimeUtils.setTime(this.timestamp).toFormat("yyyy-MM-dd 23:59:59"));
    }

    /**
     * 增加/减少 n 年
     *
     * @param amount 增加量（可以为负数，负数则是减时间）
     */
    public TimeUtils addYear(int amount) {
        return add(Calendar.YEAR, amount);
    }

    /**
     * 增加/减少 n 个月
     *
     * @param amount 增加量（可以为负数，负数则是减时间）
     */
    public TimeUtils addMonth(int amount) {
        return add(Calendar.MONTH, amount);
    }

    /**
     * 增加/减少 n 天
     *
     * @param amount 增加量（可以为负数，负数则是减时间）
     */
    public TimeUtils addDay(int amount) {
        return add(Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 增加/减少 n 小时
     *
     * @param amount 增加量（可以为负数，负数则是减时间）
     */
    public TimeUtils addHour(int amount) {
        return add(Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 增加/减少 n 分钟
     *
     * @param amount 增加量（可以为负数，负数则是减时间）
     */
    public TimeUtils addMinute(int amount) {
        return add(Calendar.MINUTE, amount);
    }

    /**
     * 增加/减少 n 秒
     *
     * @param amount 增加量（可以为负数，负数则是减时间）
     */
    public TimeUtils addSecond(int amount) {
        return add(Calendar.SECOND, amount);
    }

    /**
     * 增加/减少一段时间
     *
     * @param type   增加类型（例如：增加n分钟，Calendar.MINUTE）
     * @param amount 增加量（可以为负数，负数则是减时间）
     * @return 时间工具
     */
    public TimeUtils add(int type, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timestamp);
        calendar.add(type, amount);
        return new TimeUtils(calendar.getTimeInMillis());
    }

    /**
     * 获取时间戳
     */
    public long toTimestamp() {
        return this.timestamp;
    }

    /**
     * 计算当前时间和指定时间的差值，单位：毫秒
     *
     * @param compareTime 比对时间
     * @return 差值，单位：毫秒
     */
    public long between(TimeUtils compareTime) {
        return Math.abs(compareTime.toTimestamp() - this.timestamp);
    }

    /**
     * 计算当前时间和指定时间的差值，单位：秒
     *
     * @param compareTime 比对时间
     * @return 差值，单位：秒
     */
    public long betweenSecond(TimeUtils compareTime) {
        return between(compareTime) / 1000;
    }

    /**
     * 输出Date格式日期
     */
    public Date toDate() {
        return new Date(this.timestamp);
    }

    /**
     * 输出LocalDate格式日期
     */
    public LocalDate toLocalDate() {
        return toLocalDateTime().toLocalDate();
    }

    /**
     * 输出LocalDateTime格式时间
     */
    public LocalDateTime toLocalDateTime() {
        Instant instant = Instant.ofEpochMilli(this.timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 输出Calendar
     */
    public Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timestamp);
        return calendar;
    }

    /**
     * 输出Timestamp
     */
    public Timestamp toSqlTimestamp() {
        return new Timestamp(this.timestamp);
    }

    /**
     * 输出格式化字符串=toString(format)
     *
     * @param format 格式（如：yyyy-MM-dd HH:mm:ss 或者 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'）
     */
    public String toFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(this.timestamp));
    }

    /**
     * 输出格式化字符串=toFormat(format)
     *
     * @param format 格式（如：yyyy-MM-dd HH:mm:ss 或者 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'）
     */
    public String toString(String format) {
        return toFormat(format);
    }

    /**
     * 输出默认日期格式字符串，如：2022-01-17 10:23:26
     */
    @Override
    public String toString() {
        return toFormat("yyyy-MM-dd HH:mm:ss");
    }


    public static void main(String[] args) {
        // 使用示例
        System.out.println(TimeUtils.now());// 操作当前时间
        System.out.println(TimeUtils.setTime(new Date()));// 操作设置时间
        // 操作示例
        System.out.println(TimeUtils.now().addDay(1).toLocalDateTime());
        System.out.println(TimeUtils.now().addDay(1).toTimestamp());
        System.out.println(TimeUtils.now().addDay(-5).toFormat("yyyy-MM-dd"));
        System.out.println(TimeUtils.now().addMinute(10).toFormat("yyyy-MM-dd HH:mm:ss"));
        System.out.println("---");
        System.out.println(TimeUtils.now());
        System.out.println(TimeUtils.now().endOfDay().toDate());
        System.out.println(TimeUtils.now().endOfDay().toString());
        System.out.println(TimeUtils.now().between(TimeUtils.setTime("2025-04-24 14:05:00")));
    }
}
