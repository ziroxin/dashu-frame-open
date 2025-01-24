package com.kg.module.filesStatic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 静态资源文件表
 * </p>
 *
 * @author ziro
 * @since 2025-01-24
 */
@Getter
@Setter
@ApiModel(value = "ZFilesStaticDTO", description = "静态资源文件表")
public class ZFilesStaticDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("静态资源id")
    private String fileId;

    @ApiModelProperty("所属文件夹id")
    private String parentId;

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

    @ApiModelProperty("文件类型(0文件夹1文件)")
    private String fileType;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty("文件夹内文件列表")
    List<ZFilesStaticDTO> children;

    @ApiModelProperty("是否自动解压（用于上传压缩包时自动解压处理）")
    private boolean autoUnzip;
}
