package com.kg.component.file.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import com.kg.component.utils.GuidUtils;

import java.io.File;
import java.util.Date;


/**
 * 下载网络文件（下载到本地）
 *
 * @author ziro
 * @date 2024-04-16 16:04
 */
public class DownloadNetFileUtils {
    /**
     * 下载网络图片并压缩
     *
     * @param fileUrl 图片地址
     * @param dirName 保存目录
     * @return 文件DTO
     */
    public static FileDTO downloadImgCompress(String fileUrl, String dirName) {
        return download(fileUrl, dirName, true);
    }

    /**
     * 下载网络文件
     *
     * @param fileUrl 文件地址
     * @param dirName 保存目录
     * @return 文件DTO
     */
    public static FileDTO download(String fileUrl, String dirName) {
        return download(fileUrl, dirName, false);
    }


    /**
     * 下载网络文件（私有方法）
     *
     * @param fileUrl    文件地址
     * @param dirName    保存目录
     * @param isCompress 是否压缩图片
     * @return 文件DTO
     */
    private static FileDTO download(String fileUrl, String dirName, boolean isCompress) {
        // 获取文件扩展名
        String contentType = HttpRequest.head(fileUrl).execute().header(Header.CONTENT_TYPE);
        System.out.println(contentType);
        String extend = contentType.split("/")[1];

        // 上传文件实体
        FileDTO fileDto = new FileDTO();
        fileDto.setFileExtend(extend);
        fileDto.setFileName(GuidUtils.getUuid32() + "." + extend);
        // 组合文件名
        String savePath = FilePathConfig.SAVE_PATH
                + "/" + dirName
                + "/" + DateUtil.format(new Date(), "yyyyMMdd")
                + "/" + fileDto.getFileName();
        savePath = savePath.replaceAll("//", "/");
        // 保存文件
        FileUtil.mkParentDirs(savePath);
        File saveFile = HttpUtil.downloadFileFromUrl(fileUrl, savePath);
        // 压缩图片
        if (isCompress) {
            if (FilePathConfig.DEFAULT_IMAGE_FILE_EXTEND.toLowerCase().indexOf(extend) >= 0) {
                // 判断是否图片格式
                Img.from(saveFile).setQuality(0.6).write(saveFile);
            }
        }
        // 文件大小
        fileDto.setFileSize(saveFile.length());
        // 文件地址
        fileDto.setFileUrl(FilePathConfig.switchUrl(savePath));
        return fileDto;
    }
}
