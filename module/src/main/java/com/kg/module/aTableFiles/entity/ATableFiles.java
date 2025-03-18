package com.kg.module.aTableFiles.entity;

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
 * 我的表a_table附件表
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@Getter
@Setter
@TableName("a_table_files")
@ApiModel(value = "ATableFiles对象", description = "我的表a_table附件表")
public class ATableFiles implements BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("附件id")
    @TableId(value = "file_id", type = IdType.ASSIGN_UUID)
    private String fileId;

    @ApiModelProperty("主表id")
    @TableField("a_table_id")
    private String aTableId;

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

    @ApiModelProperty("附件上传时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
