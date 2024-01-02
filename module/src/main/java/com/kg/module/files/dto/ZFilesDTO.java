package com.kg.module.files.dto;

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
 * 文件记录表
 * </p>
 *
 * @author ziro
 * @since 2023-09-15
 */
@Getter
@Setter
@ApiModel(value = "ZFilesDTO", description = "文件记录表")
public class ZFilesDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件id")
    private String fileId;

    @ApiModelProperty("文件md5")
    private String fileMd5;

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

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
