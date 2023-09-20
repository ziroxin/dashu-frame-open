package com.kg.component.file.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 获取文件MD5
 *
 * @author ziro
 * @date 2023/9/15 14:50
 */
public class FileMD5Utils {

    public static String getFileMD5(String filePath) throws FileNotFoundException {
        return getFileMD5(new FileInputStream(filePath));
    }

    public static String getFileMD5(File file) throws FileNotFoundException {
        return getFileMD5(new FileInputStream(file));
    }

    public static String getFileMD5(InputStream is) throws FileNotFoundException {
        return getFileMD5((FileInputStream) is);
    }

    public static String getFileMD5(FileInputStream fis) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = fis.read(buffer)) != -1) {
                md.update(buffer, 0, n);
            }
            byte[] md5Bytes = md.digest();
            // 将字节数组转换为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                sb.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
            }
            fis.close();
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}