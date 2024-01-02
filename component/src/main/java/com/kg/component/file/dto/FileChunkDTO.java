package com.kg.component.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 文件分片上传 - 数据实体类
 *
 * @author ziro
 * @date 2022-07-02 14:56:27
 */
@Getter
@Setter
@ToString
public class FileChunkDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 文件md5信息
     */
    private String fileMd5;
    /**
     * 文件地址（文件访问地址）
     */
    private String fileUrl;
    /**
     * 原文件名
     */
    private String fileOldName;
    /**
     * 存储文件名
     */
    private String fileName;
    /**
     * 文件扩展名
     */
    private String fileExtend;
    /**
     * 文件大小
     */
    private long fileSize;
    /**
     * 分片编号（第几片）
     */
    private String chunkNumber;
    /**
     * 是否已合并（全部上传完成）
     */
    private boolean isMerged;
}
