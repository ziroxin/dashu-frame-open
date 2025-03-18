package com.kg.module.aTableFiles.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 我的表a_table附件表
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@Getter
@Setter
@ApiModel(value = "ATableFilesDTO", description = "我的表a_table附件表")
public class ATableFilesDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("附件id")
    private String fileId;

    @ApiModelProperty("主表id")
    private String aTableId;

    @ApiModelProperty("文件地址（文件访问地址）")
    private String fileUrl;

    @ApiModelProperty("原文件名")
    private String fileOldName;

    @ApiModelProperty("存储文件名")
    private String fileName;

    @ApiModelProperty("文件扩展名")
    private String fileExtend;

    @ApiModelProperty("文件大小")
    private Long fileSize;

    @ApiModelProperty("附件上传时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
