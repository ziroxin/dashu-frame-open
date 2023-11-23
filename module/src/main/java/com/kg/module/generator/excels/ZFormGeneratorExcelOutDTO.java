package com.kg.module.generator.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 代码生成器表单
 *
 * @author ziro
 * @date 2023-11-22
 */
@Getter
@Setter
public class ZFormGeneratorExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 表单id
     */
    private String formId;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单内容json格式
     */
    private String formJson;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableDecription;

    /**
     * pom模块名
     */
    private String basePackage;

    /**
     * 作者
     */
    private String author;

    /**
     * 生成包名
     */
    private String tablePackage;

    /**
     * 前端路径
     */
    private String viewPath;

    /**
     * 代码生成状态（0未生成1已生成）
     */
    private String status;

    /**
     * 显示顺序
     */
    private Integer orderIndex;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}