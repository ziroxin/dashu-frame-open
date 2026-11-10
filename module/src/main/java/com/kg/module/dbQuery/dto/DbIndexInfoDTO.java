package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据库索引信息 DTO
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("数据库索引信息")
public class DbIndexInfoDTO {

    @ApiModelProperty("索引名")
    private String indexName;

    @ApiModelProperty("是否唯一")
    private Boolean nonUnique;

    @ApiModelProperty("索引类型（BTREE/HASH等）")
    private String indexType;

    @ApiModelProperty("索引列")
    private List<String> columns;

    @ApiModelProperty("索引注释")
    private String indexComment;
}
