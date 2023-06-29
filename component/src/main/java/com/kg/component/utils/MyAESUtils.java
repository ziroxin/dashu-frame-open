package com.kg.component.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * Aes方式，对称加密、解密
 *
 * @author ziro
 * @date 2020/8/11 14:21
 */
public class MyAESUtils {
    /**
     * 密钥, AES-128 需16个字符, AES-256 需要32个字符
     */
    private static final String AES_KEY_DEFAULT = "dashu-frame-2023dashu-frame-2023";

    /**
     * 加密
     */
    public static String encrypt(String value) {
        return encrypt(value, AES_KEY_DEFAULT);
    }

    /**
     * 加密（自定义密钥）
     */
    public static String encrypt(String value, String keyStr) {
        // 密钥
        byte[] key = keyStr.getBytes();
        // 构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        return aes.encryptHex(value);
    }

    /**
     * 解密
     */
    public static String decrypt(String value) {
        return decrypt(value, AES_KEY_DEFAULT);
    }

    /**
     * 解密（自定义密钥)
     */
    public static String decrypt(String value, String keyStr) {
        // 密钥
        byte[] key = keyStr.getBytes();
        // 构建
        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        return des.decryptStr(value, CharsetUtil.CHARSET_UTF_8);
    }

    public static void main(String[] args) {
        String a = "1111";
        String encrypt = MyAESUtils.encrypt(a);
        System.out.println(encrypt);
        String decrypt = MyAESUtils.decrypt(encrypt);
        System.out.println(decrypt);
    }

}
