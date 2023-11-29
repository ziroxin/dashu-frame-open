package com.kg.core.formGenerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 表信息和字段信息
 *
 * @author ziro
 * @date 2023/11/27 16:47
 */
@Getter
@Setter
public class TableInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    private String tableName;
    /**
     * 表注释
     */
    private String tableComment;
    /**
     * 字段列表
     */
    private List<TableFieldDTO> fieldList;
}
