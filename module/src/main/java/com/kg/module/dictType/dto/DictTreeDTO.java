package com.kg.module.dictType.dto;

import com.kg.module.dictData.entity.ZDictData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 数据字典树
 *
 * @author ziro
 * @date 2025/7/17 10:39
 */
@Getter
@Setter
@ApiModel(value = "DictTreeDTO", description = "数据字典树")
public class DictTreeDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典类型ID")
    private String typeId;

    @ApiModelProperty("字典名称")
    private String typeName;

    @ApiModelProperty("字典类型code")
    private String typeCode;

    @ApiModelProperty("字典数据")
    private List<ZDictData> children;
}
