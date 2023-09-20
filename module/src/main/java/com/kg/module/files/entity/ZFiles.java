package com.kg.module.files.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@TableName("z_files")
@ApiModel(value = "ZFiles对象", description = "文件记录表")
public class ZFiles implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件id")
    @TableId(value = "file_id", type = IdType.ASSIGN_UUID)
    private String fileId;

    @ApiModelProperty("文件md5")
    @TableField("file_md5")
    private String fileMd5;

    @ApiModelProperty("文件地址（文件访问地址）")
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty("原文件名")
    @TableField("file_old_name")
    private String fileOldName;

    @ApiModelProperty("存储文件名")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty("文件扩展名")
    @TableField("file_extend")
    private String fileExtend;

    @ApiModelProperty("文件大小")
    @TableField("file_size")
    private Long fileSize;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
