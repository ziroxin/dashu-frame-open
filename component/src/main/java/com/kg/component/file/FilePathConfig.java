package com.kg.component.file;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件名操作 - 工具类
 *
 * @author ziro
 * @date 2022-07-02 11:13:28
 */
@Component
public class FilePathConfig extends FilenameUtils {
    /**
     * 允许上传的文件扩展名
     */
    public static String UPLOAD_FILE_ALLOW_EXTEND;
    /**
     * 图片格式扩展名
     */
    public static String DEFAULT_IMAGE_FILE_EXTEND;
    /**
     * 文件存储绝对路径
     */
    public static String SAVE_PATH;
    /**
     * 文件上传url前缀
     */
    public static String URL_PRE;

    /**
     * 设置允许上传的文件扩展名
     */
    @Value("${com.kg.file.allow-extend}")
    public void setUploadFileAllowExtend(String extend) {
        FilePathConfig.UPLOAD_FILE_ALLOW_EXTEND = extend;
    }

    /**
     * 图片格式扩展名
     */
    @Value("${com.kg.file.image-extend}")
    public void setDefaultImageFileExtend(String imageExtend) {
        FilePathConfig.DEFAULT_IMAGE_FILE_EXTEND = imageExtend;
    }

    /**
     * 文件存储绝对路径
     */
    @Value("${com.kg.file.save-path}")
    public void setSavePath(String savePath) {
        FilePathConfig.SAVE_PATH = savePath;
    }

    /**
     * 文件访问路径（替换真实存储路径）
     */
    @Value("${com.kg.file.url-pre}")
    public void setUrlPre(String urlPre) {
        FilePathConfig.URL_PRE = urlPre;
    }

    /**
     * 获取文件访问地址
     *
     * @param savePath 文件保存路径
     * @return 文件访问地址
     */
    public static String switchUrl(String savePath) {
        if (savePath.startsWith(FilePathConfig.SAVE_PATH)) {
            savePath = savePath.replaceFirst(FilePathConfig.SAVE_PATH, FilePathConfig.URL_PRE);
            return savePath;
        }
        return savePath;
    }

    /**
     * 获取文件存储路径
     *
     * @param url 文件访问地址
     * @return 文件存储路径
     */
    public static String switchSavePath(String url) {
        if (url.startsWith(FilePathConfig.URL_PRE)) {
            url = url.replaceFirst(FilePathConfig.URL_PRE, FilePathConfig.SAVE_PATH);
            return url;
        }
        return url;
    }

    /**
     * 删除上传文件名中的特殊字符
     *
     * @param fileName 原文件名
     * @return 新文件名
     */
    public static String removeSpec(String fileName) {
        return fileName.replaceAll(",", "")
                .replaceAll("\\|", "")
                .replaceAll("!", "")
                .replaceAll("@", "")
                .replaceAll("#", "")
                .replaceAll("\\$", "")
                .replaceAll("%", "")
                .replaceAll("\\^", "")
                .replaceAll("&", "");
    }
}
