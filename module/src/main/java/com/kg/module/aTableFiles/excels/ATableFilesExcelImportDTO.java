package com.kg.module.aTableFiles.excels;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导入Excel实体 - 我的表a_table附件表
 *
 * @author ziro
 * @date 2025-03-18
 */
@Getter
@Setter
public class ATableFilesExcelImportDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    /** 主表id */
    private String aTableId;

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
}