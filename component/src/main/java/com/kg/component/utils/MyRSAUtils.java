package com.kg.component.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

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
public class MyRSAUtils {
    /**
     * 公钥
     */
    private static final String RSA_PUBLIC_KEY_DEFAULT = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdSR523juSOUKRfyCwxLMnueQ5HbFH3ORgSzsIFIuz40lUIeZqRPjNkFFBbV159XQfXGs0pfoX68Y+ylTdWaCCeHHxNjx+qVkAE9tMyosxDXSUj+Yz8ruZA920u0ne2VSVZd41AtrlLaM8DaFVOfC7dngFx12rosfSlxrx0yPU8wIDAQAB";

    /**
     * 私钥
     */
    private static final String RSA_PRIVATE_KEY_DEFAULT = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ1JHnbeO5I5QpF/ILDEsye55DkdsUfc5GBLOwgUi7PjSVQh5mpE+M2QUUFtXXn1dB9cazSl+hfrxj7KVN1ZoIJ4cfE2PH6pWQAT20zKizENdJSP5jPyu5kD3bS7Sd7ZVJVl3jUC2uUtozwNoVU58Lt2eAXHXauix9KXGvHTI9TzAgMBAAECgYBF2l1vSU+Hp2qLF7y7BQDUGdjkDO3ZDp9WrNKwyf8piz3b4Zplg/BDy15rAllLetlxvCfYoAYsbYgEBvQdwlpoINyt3WjCfwaOH6CRI2AtgI90D6qLk+1ejQuepzzitCKId5gvu0uOQbYxo4YMiszDzH15FjqnLODQ7VSWO2vCYQJBAPpVxeGZ2C0hFNgNZWF5wGubMqEYXdMOqO2B/Fo1k3oO8TWhuT+4PupXMHtvVWWu1py/KzVTAGZNyR1CiZyjnMsCQQCg2EwaBF5UUCYk0xYmXpp5c7ysnzErgYBztihWbFrX2LsgNInN6EBvjT4hNjz3sQcfwfy3jDac5HIocGVl9gt5AkBZRIDd5Ahsf2F5cb13Nv1g4eT6AUBj5NRkbXfZi0VdvzpQj60JheAZnKHTQm5HZkNtfdYgm8qGsLJI1tNPoiBlAkBJAcSRLphuZq7ZmiJ8qgYtyHXEWMGlwLNAUlc+2xHk1VAijxZZqujMeWzRAGBuASF9rbx6x57mWd7jMhkkTajpAkEApVakMWfoALWG/zigX6CiuIDXuPqn1Qh73AR5C3ypPUNbYCReJx9G5gxDohtqiHBxY9rYqR1wR7pWFanmMTGlPw==";

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
        return rsa.decryptStr(data, KeyType.PublicKey);
    }

    public static void main(String[] args) {
        String str = "abcdziro1234";
        // 1.公钥加密，私钥解密
        String enStr = encryptPublic(str);
        System.out.println(enStr);
        System.out.println(decryptPrivate(enStr));
        // 2.私钥加密，公钥解密
        String enStr2 = encryptPrivate(str);
        System.out.println(enStr2);
        System.out.println(decryptPublic(enStr2));

        // 3.生成公钥和私钥
        RSA rsa = new RSA();
        System.out.println("--- 公钥base64 ---");
        System.out.println(rsa.getPublicKeyBase64());
        System.out.println("--- 私钥base64 ---");
        System.out.println(rsa.getPrivateKeyBase64());
    }
}
