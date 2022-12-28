package com.kg.core.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

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
    LOGIN_ERROR_TOKEN_INVALID(40002, "无效的TOKEN！"),
    LOGIN_ERROR_NOT_LOGIN(40003, "用户未登录！"),
    SERVER_ERROR(500, "服务器端错误！");

    private Integer code;
    private String info;

    /**
     * 根据code，查询枚举信息
     */
    public static BaseErrorCode getByCode(Integer code) {
        Optional<BaseErrorCode> first = Arrays.stream(BaseErrorCode.values()).filter(o -> o.getCode() == code).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }
}
