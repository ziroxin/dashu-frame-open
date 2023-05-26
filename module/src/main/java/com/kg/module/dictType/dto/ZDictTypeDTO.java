package com.kg.module.dictType.dto;

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
 * 字典类型
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@Getter
@Setter
@ApiModel(value = "ZDictTypeDTO", description = "字典类型")
public class ZDictTypeDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典类型ID")
    private String typeId;

    @ApiModelProperty("字典名称")
    private String typeName;

    @ApiModelProperty("字典类型code")
    private String typeCode;

    @ApiModelProperty("状态0停用1正常")
    private String status;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
