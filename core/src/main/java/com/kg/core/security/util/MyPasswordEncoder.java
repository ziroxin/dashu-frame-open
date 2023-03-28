package com.kg.core.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 重写Security用的加密方法
 * 重写原因：
 * 当无法获取到明文密码时，可以使用密文验证密码是否正确
 *
 * @author ziro
 * @date 2023-03-28 15:35:42
 */
public class MyPasswordEncoder implements PasswordEncoder {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 密码加密方法
     *
     * @param rawPassword
     * @return
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 重写判断方法，即判断明文密码是否匹配，也判断加密后的密码是否匹配
     *
     * @param rawPassword     待比较密码
     * @param encodedPassword 加密后密码
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword)
                    // 传过来的 rawPassword 是密文时，直接验证密文是否与数据库中一致
                    || rawPassword.toString().equals(encodedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
