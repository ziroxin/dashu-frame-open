package com.kg.core.formGenerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 表单生成代码：表信息实体
 *
 * @author ziro
 * @date 2023-02-03 15:45:03
 */
@Getter
@Setter
public class TableDTO {
    /** 表名 */
    private String tableName;
    /** 表注释 */
    private String tableDecription;
    /** 后台项目根模块 */
    private String basePackage;
    /** 作者 */
    private String author;
    /** 本模块包名 */
    private String tablePackage;
    /** vue项目，views下的路径（同时用于生成权限sql） */
    private String viewPath;

    /** 是否生成[删除日志]功能相关代码 */
    private Boolean isDeleteLogs;

    /** 查询字段列表 */
    private List<String> searchFields;
    /** 列表字段列表 */
    private List<String> listFields;
    /** 导入字段列表 */
    private List<String> importFields;
    /** 导出字段列表 */
    private List<String> exportFields;

    /** 字段信息 */
    private List<TableFieldDTO> fields;

    /** 代码生成html模板 */
    private String template;
    /** 代码生成jsData */
    private String jsData;
    /** 代码生成jsCreated */
    private String jsCreated;
    /** 代码生成jsMethod */
    private String jsMethods;
    /** 代码生成css */
    private String css;

    /** 是否覆盖数据库表 */
    private Boolean isCoverTable;

    /** 表单管理表id */
    private String formId;

    /**
     * 代码生成类型
     * generateType='code' 时，表示com.kg.generator.MybatisPlusGenerator工具，生成前端代码
     * 其他使用情况，用到时在这里增加注释
     */
    private String generateType;
    // =============== 以下字段，均在 generateType='code' 时使用 ===================
    // el-form-item附件字段名
    private Map<String, String> attachmentField;
    // 查询字段
    private Map<String, String> searchMap;
    // 列表字段
    private Map<String, String> listMap;
    // 导入字段
    private Map<String, String> importMap;
    // 导出字段
    private Map<String, String> exportMap;

}
