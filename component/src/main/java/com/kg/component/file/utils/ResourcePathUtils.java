package com.kg.component.file.utils;

/**
 * Java 的 resources 文件夹工具类
 *
 * @author ziro
 * @date 2024/4/7 14:09
 */
public class ResourcePathUtils {

    /**
     * 获取resources目录根路径
     *
     * @return 绝对路径
     */
    public static String path() {
        return ResourcePathUtils.class.getResource("/").getPath();
    }

    /**
     * 获取resources下，指定文件或目录的根路径
     *
     * @param fileName 文件或目录（例如：static或static/xxx.html）不要带开头/
     * @return 绝对路径
     */
    public static String path(String fileName) {
        return ResourcePathUtils.class.getResource("/" + fileName).getPath();
    }
}
