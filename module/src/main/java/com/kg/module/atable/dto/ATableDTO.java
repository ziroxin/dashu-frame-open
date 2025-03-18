package com.kg.module.atable.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.kg.module.aTableImages.entity.ATableImages;
import com.kg.module.aTableFiles.entity.ATableFiles;
import java.util.List;

/**
 * <p>
 * 我的表a_table
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@Getter
@Setter
@ApiModel(value = "ATableDTO", description = "我的表a_table")
public class ATableDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("姓名")
    private String testName;

    @ApiModelProperty("ImageAvatar")
    private String field118;

    @ApiModelProperty("ImageOne")
    private String field119;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private List<ATableImages> atableimagesList;

    private List<ATableFiles> atablefilesList;
}
