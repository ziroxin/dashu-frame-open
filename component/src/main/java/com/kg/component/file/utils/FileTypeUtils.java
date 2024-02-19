package com.kg.component.file.utils;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 获取文件真实类型 - 工具类
 *
 * @author ziro
 * @date 2022-04-07 18:14:17
 */
public class FileTypeUtils {
    /**
     * 常见文件头map
     */
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    static {
        // 初始化：常见文件头信息
        loadFileType();
    }

    /**
     * 加载文件类型
     */
    public static void loadFileType() {
        FILE_TYPE_MAP.clear();
        // 读取忽略列表
        List<String> antMatchers = FileUtil.readLines("fileTypeMap.properties", CharsetUtil.defaultCharset());
        antMatchers.stream().filter(str -> StringUtils.hasText(str) && !str.startsWith("#")).forEach(str -> {
            String[] split = str.split("\\|");
            if (split.length == 2) {
                FILE_TYPE_MAP.put(split[0], split[1]);
            }
        });
    }

    /**
     * 获取文件类型
     *
     * @param filePath 文件地址
     * @return 文件类型
     */
    public static String getFileType(String filePath) throws IOException {
        return getFileType(new FileInputStream(filePath));
    }

    /**
     * 获取文件类型
     *
     * @param file 文件
     * @return 文件类型
     */
    public static String getFileType(File file) throws IOException {
        return getFileType(new FileInputStream(file));
    }

    /**
     * 获取文件类型
     *
     * @param fis 文件流
     * @return 文件类型
     */
    public static String getFileType(FileInputStream fis) throws IOException {
        byte[] bytes = new byte[10];
        fis.read(bytes, 0, bytes.length);
        return assertFileType(bytes);
    }

    /**
     * 获取文件类型
     *
     * @param fileBytes 文件bytes
     * @return 文件类型
     */
    public static String getFileType(byte[] fileBytes) throws IOException {
        byte[] bytes = new byte[10];
        for (int i = 0; i < bytes.length; i++) {
            if (fileBytes.length == 0 || fileBytes.length <= i) {
                break;
            }
            bytes[i] = fileBytes[i];
        }
        return assertFileType(bytes);
    }

    /**
     * （私有）判断文件类型
     * 与文件头FILE_TYPE_MAP比对，返回文件类型
     *
     * @param bytes 文件头bytes
     * @return 文件类型
     */
    private static String assertFileType(byte[] bytes) {
        String fileCode = getFileHeader(bytes);
        String res = null;
        //这种方法在字典的头代码不够位数的时候可以用但是速度相对慢一点
        Iterator<String> keyIter = FILE_TYPE_MAP.keySet().iterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            if (key.toLowerCase().startsWith(fileCode.toLowerCase()) || fileCode.toLowerCase().startsWith(key.toLowerCase())) {
                res = FILE_TYPE_MAP.get(key);
                break;
            }
        }
        return res == null ? null : res.toLowerCase();
    }

    /**
     * （私有）获取文件头
     *
     * @param bytes 文件头bytes
     * @return 返回文件头Hex字符串
     */
    public static String getFileHeader(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 测试文件类型，获取文件头
     */
    public static void main(String[] args) throws Exception {

//        String type = getFileType("E:\\Users\\ziro\\Desktop\\ccxs.xlsx");
//        System.out.println(type);

        File[] ls = FileUtil.ls("E:\\");
        for (int i = 0; i < ls.length; i++) {
            String type2 = ls[i].getName().substring(ls[i].getName().lastIndexOf(".") + 1);
            String type1 = null;
            try {
                FileInputStream fis = new FileInputStream(ls[i]);
                type1 = FileTypeUtils.getFileType(fis);
            } catch (IOException e) {
                continue;
//                e.printStackTrace();
            }
            if (type1 == null) {
                System.out.println("------------------------------------------------------------------------------------------------------" +
                        type2 + "==============" + ls[i].getName());
                continue;
            }
            System.out.print(type2);
//            System.out.print(ls[i].getName());
            System.out.print("               ");
            System.out.print(type1.contains(type2));
            System.out.print("               ");
            System.out.print(type1);
            System.out.print("               ");
            byte[] bytes = new byte[10];
            IoUtil.toStream(ls[i]).read(bytes, 0, bytes.length);
            System.out.print(FileTypeUtils.getFileHeader(bytes));
            System.out.println("");
        }

    }

}
