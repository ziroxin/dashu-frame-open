package com.kg.module.dictData.dto;

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
 * 字典数据
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@Getter
@Setter
@ApiModel(value = "ZDictDataDTO", description = "字典数据")
public class ZDictDataDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典数据ID")
    private String dictId;

    @ApiModelProperty("所属类型code")
    private String typeCode;

    @ApiModelProperty("字典标签")
    private String dictLabel;

    @ApiModelProperty("字典值")
    private String dictValue;

    @ApiModelProperty("状态0停用1正常")
    private String status;

    @ApiModelProperty("顺序")
    private Integer orderIndex;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
