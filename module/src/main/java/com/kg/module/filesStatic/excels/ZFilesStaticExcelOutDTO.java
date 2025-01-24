package com.kg.module.filesStatic.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 静态资源文件表
 *
 * @author ziro
 * @date 2025-01-24
 */
@Getter
@Setter
public class ZFilesStaticExcelOutDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    /** 所属文件夹id */
    private String parentId;

    /** 文件md5 */
    private String fileMd5;

    /** 文件地址（文件访问地址） */
    private String fileUrl;

    /** 原文件名 */
    private String fileOldName;

    /** 存储文件名 */
    private String fileName;

    /** 文件扩展名 */
    private String fileExtend;

    /** 文件大小 */
    private Long fileSize;

    /** 文件类型(0文件夹1文件) */
    private String fileType;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}