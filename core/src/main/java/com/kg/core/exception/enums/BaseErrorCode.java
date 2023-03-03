package com.kg.core.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一自定义异常码
 *
 * @author ziro
 * @date 2022-12-28 11:06:49
 */
@Getter
@AllArgsConstructor
public enum BaseErrorCode {
    LOGIN_ERROR_USERNAME_OR_PASSWORD_WRONG(40001, "用户名或者密码错误！"),
    LOGIN_ERROR_TOKEN_INVALID(40002, "您未登录或者登录已过期！请重新登录"),// 无效的TOKEN！
    LOGIN_ERROR_NOT_LOGIN(40003, "用户未登录！"),
    LOGIN_ERROR_USER_DISABLED(40004, "用户已禁用！"),
    SERVER_ERROR(500, "服务器端错误！");

    private Integer code;
    private String info;

    private static Map<Integer, BaseErrorCode> codeMap = new HashMap<>();

    static {
        for (BaseErrorCode value : BaseErrorCode.values()) {
            codeMap.put(value.getCode(), value);
        }
    }

    /**
     * 根据code，查询枚举信息
     */
    public static BaseErrorCode getByCode(Integer code) {
        return codeMap.get(code);
    }
}
