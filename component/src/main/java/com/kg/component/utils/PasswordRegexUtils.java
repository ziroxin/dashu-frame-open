package com.kg.component.utils;

import java.util.regex.Pattern;

/**
 * 密码安全性规则验证工具
 *
 * @author ziro
 * @date 2023/1/4 10:18:49
 */
public class PasswordRegexUtils {

    /**
     * 密码长度判断
     *
     * @param min      最小长度
     * @param max      最大长度
     * @param password 待验证密码
     * @return 是否符合规则
     */
    public static boolean judgeLength(int min, int max, String password) {
        if (min <= max) {
            String regex = ".{" + min + "," + max + "}";
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(password).matches();
        }
        return false;
    }

    /**
     * 密码强度判断
     *
     * @param lowerStatus   0无 1必须 2非必须
     * @param upperStatus   0无 1必须 2非必须
     * @param numStatus     0无 1必须 2非必须
     * @param specialStatus 0无 1必须 2非必须
     * @param password      待验证密码
     * @return 是否符合规则
     */
    public static boolean judgeRegex(Integer lowerStatus, Integer upperStatus, Integer numStatus, Integer specialStatus, String password) {
        if (lowerStatus != null) {
            String regex = PasswordRegexUtils.regexLowerCase(lowerStatus);
            Pattern pattern = Pattern.compile(regex);
            boolean flag = pattern.matcher(password).matches();
            if (!flag) {
                return flag;
            }
        }
        if (upperStatus != null) {
            String regex = PasswordRegexUtils.regexUpperCase(upperStatus);
            Pattern pattern = Pattern.compile(regex);
            boolean flag = pattern.matcher(password).matches();
            if (!flag) {
                return flag;
            }
        }
        if (numStatus != null) {
            String regex = PasswordRegexUtils.regexNumber(numStatus);
            Pattern pattern = Pattern.compile(regex);
            boolean flag = pattern.matcher(password).matches();
            if (!flag) {
                return flag;
            }
        }
        if (specialStatus != null) {
            String regex = PasswordRegexUtils.regexSpecial(specialStatus);
            Pattern pattern = Pattern.compile(regex);
            boolean flag = pattern.matcher(password).matches();
            if (!flag) {
                return flag;
            }
        }
        return true;
    }

    /**
     * 小写字母，正则表达式规则
     *
     * @param status 状态 0无 1必须 2非必须
     * @return 表达式
     */
    private static String regexLowerCase(int status) {
        try {
            if (status == 1) {
                //必须
                return "(?=.*[a-z]).*";
            } else if (status == 2) {
                //非必须
                return ".*";
            } else {
                //无
                return "(?!.*[a-z]).*";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "(?!.*[a-z]).*";
    }

    /**
     * 大写字母，正则表达式规则
     *
     * @param status 状态 0无 1必须 2非必须
     * @return 表达式
     */
    private static String regexUpperCase(int status) {
        try {
            if (status == 1) {
                //必须
                return "(?=.*[A-Z]).*";
            } else if (status == 2) {
                //非必须
                return ".*";
            } else {
                //无
                return "(?!.*[A-Z]).*";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "(?!.*[A-Z]).*";
    }

    /**
     * 数字，正则表达式规则
     *
     * @param status 状态 0无 1必须 2非必须
     * @return 表达式
     */
    private static String regexNumber(int status) {
        try {
            if (status == 1) {
                //必须
                return "(?=.*[0-9]).*";
            } else if (status == 2) {
                //非必须
                return ".*";
            } else {
                //无
                return "(?!.*[0-9]).*";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "(?!.*[0-9]).*";
    }

    /**
     * 特殊字符，正则表达式规则
     *
     * @param status 状态 0无 1有
     * @return 表达式
     */
    private static String regexSpecial(int status) {
        try {
            if (status == 1) {
                //必须
                return "(?=.*[*?!&￥$%^#,.@;:\\-=+_\\|》《。，、？！~]).*";// 必须
            } else if (status == 2) {
                //非必须
                return ".*";
            } else {
                //无
                return "[a-zA-Z0-9]*";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[a-zA-Z0-9]*";
    }
}
