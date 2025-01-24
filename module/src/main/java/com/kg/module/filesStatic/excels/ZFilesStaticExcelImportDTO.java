package com.kg.module.filesStatic.excels;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导入Excel实体 - 静态资源文件表
 *
 * @author ziro
 * @date 2025-01-24
 */
@Getter
@Setter
public class ZFilesStaticExcelImportDTO implements BaseDTO {
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
    private String fileSize;

    /** 文件类型(0文件夹1文件) */
    private String fileType;
}