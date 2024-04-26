package com.kg.component.file.utils;

/**
 * Java 的 resources 文件夹工具类
 *
 * @author ziro
 * @date 2024/4/7 14:09
 */
public class ResourcePathUtils {

    /**
     * 获取资源根路径
     */
    public String path() {
        return ResourcePathUtils.class.getResource("/").getPath();
    }

    /**
     * 获取资源根路径
     *
     * @param fileName 文件或目录
     */
    public String path(String fileName) {
        return ResourcePathUtils.class.getResource("/" + fileName).getPath();
    }
}
