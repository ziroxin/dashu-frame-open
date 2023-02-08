package com.kg.core.formGenerator.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 表单生成代码：字段信息实体
 *
 * @author ziro
 * @date 2023-02-03 15:47:20
 */
@Getter
@Setter
public class TableFieldDTO {
    /**
     * 字段名
     */
    private String name;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 字段长度
     */
    private int length;
    /**
     * 小数点位数
     */
    private int point;
    /**
     * 是否必填
     */
    private boolean required;
    /**
     * 是否主键
     */
    private boolean key;
    /**
     * 注释
     */
    private String title;
    /**
     * 附件，子表名
     */
    private String childFileTable;
}
