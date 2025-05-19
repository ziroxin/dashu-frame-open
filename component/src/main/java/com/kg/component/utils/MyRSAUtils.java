package com.kg.component.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RSA方式，非对称加密、解密
 * <p>
 * 注意：
 * 公钥加密，必须用私钥解密
 * 私钥加密，必须用公钥解密
 *
 * @author ziro
 * @date 2023/6/29 9:57
 */
@Component
public class MyRSAUtils {
    /** 公钥 */
    private static String RSA_PUBLIC_KEY_DEFAULT;
    /** 私钥 */
    private static String RSA_PRIVATE_KEY_DEFAULT;
    /** 分块加密的分隔符 */
    private static String RSA_SEPARATOR;

    @Value("${com.kg.rsa.publicKey}")
    public void setRsaPublicKey(String publicKey) {
        MyRSAUtils.RSA_PUBLIC_KEY_DEFAULT = publicKey;
    }

    @Value("${com.kg.rsa.privateKey}")
    public void setRsaPrivateKey(String privateKey) {
        MyRSAUtils.RSA_PRIVATE_KEY_DEFAULT = privateKey;
    }

    @Value("${com.kg.rsa.separator}")
    public void setRsaSeparator(String separator) {
        MyRSAUtils.RSA_SEPARATOR = separator;
    }

    /**
     * 公钥加密
     *
     * @param value 要加密的字符串
     * @return 加密后的字符串
     */
    public static String encryptPublic(String value) {
        return encryptPublic(value, RSA_PUBLIC_KEY_DEFAULT);
    }

    /**
     * 公钥加密 - 自定义公钥
     *
     * @param value     要加密的字符串
     * @param publicKey 公钥
     * @return 加密后的字符串
     */
    public static String encryptPublic(String value, String publicKey) {
        RSA rsa = new RSA(null, publicKey);
        return rsa.encryptBase64(value, KeyType.PublicKey);
    }

    /**
     * 私钥解密
     *
     * @param data 要解密的字符串
     * @return 解密后的字符串
     */
    public static String decryptPrivate(String data) {
        return decryptPrivate(data, RSA_PRIVATE_KEY_DEFAULT);
    }

    /**
     * 私钥解密 - 自定义私钥
     *
     * @param data       要解密的字符串
     * @param privateKey 私钥
     * @return 解密后的字符串
     */
    public static String decryptPrivate(String data, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        // 前端分块加密时，需要分块解密，然后拼接
        if (data.contains(RSA_SEPARATOR)) {
            String[] arr = data.split(RSA_SEPARATOR);
            StringBuilder sb = new StringBuilder();
            for (String s : arr) {
                sb.append(rsa.decryptStr(s, KeyType.PrivateKey));
            }
            return sb.toString();
        }
        return rsa.decryptStr(data, KeyType.PrivateKey);
    }


    /**
     * 私钥加密
     *
     * @param value 要加密的字符串
     * @return 加密后的字符串
     */
    public static String encryptPrivate(String value) {
        return encryptPrivate(value, RSA_PRIVATE_KEY_DEFAULT);
    }

    /**
     * 私钥加密 - 自定义私钥
     *
     * @param value      要加密的字符串
     * @param privateKey 私钥
     * @return 加密后的字符串
     */
    public static String encryptPrivate(String value, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        return rsa.encryptBase64(value, KeyType.PrivateKey);
    }

    /**
     * 公钥解密
     *
     * @param data 要解密的字符串
     * @return 解密后的字符串
     */
    public static String decryptPublic(String data) {
        return decryptPublic(data, RSA_PUBLIC_KEY_DEFAULT);
    }

    /**
     * 公钥解密 - 自定义公钥
     *
     * @param data      要解密的字符串
     * @param publicKey 公钥
     * @return 解密后的字符串
     */
    public static String decryptPublic(String data, String publicKey) {
        RSA rsa = new RSA(null, publicKey);
        // 前端分块加密时，需要分块解密，然后拼接
        if (data.contains(RSA_SEPARATOR)) {
            String[] arr = data.split(RSA_SEPARATOR);
            StringBuilder sb = new StringBuilder();
            for (String s : arr) {
                sb.append(rsa.decryptStr(s, KeyType.PublicKey));
            }
            return sb.toString();
        }
        return rsa.decryptStr(data, KeyType.PublicKey);
    }

    public static void main(String[] args) {
        // @ziroxin 2025-5-19
        // 由于公钥和私钥改为配置文件配置，main内不能直接调用加密解密方法
        // 可用示例见：见 com.kg.other.OtherTest.java 单元测试类 testMyRSA() 方法

        // 生成公钥和私钥
        RSA rsa = new RSA();
        System.out.println("--- 公钥base64 ---");
        System.out.println(rsa.getPublicKeyBase64());
        System.out.println("--- 私钥base64 ---");
        System.out.println(rsa.getPrivateKeyBase64());
    }
}
