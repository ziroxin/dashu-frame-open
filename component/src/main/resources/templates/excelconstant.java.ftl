package ${package.ExcelConstant};

import java.util.LinkedHashMap;

/**
 * Excel使用常量 - ${table.comment!}
 *
 * @author ${author}
 * @date ${date}
 */
public class ${entity}ExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();
    /**
     * 导入所需字段信息
     */
    public static LinkedHashMap<String, String> IMPORT_EXCEL_COLUMN = new LinkedHashMap<>();
    /**
     * 导入必填字段信息
     */
    public static LinkedHashMap<String, String> IMPORT_REQUIRED_COLUMN = new LinkedHashMap<>();

    static {
<#if exportFields??>
<#-- 根据前端配置exportFields - 生成导出Excel字段 -->
        // 初始化导出字段
  <#list table.fields as field>
    <#if exportFields?seq_contains(field.annotationColumnName)>
        EXPORT_EXCEL_COLUMN.put("${field.propertyName}", "${field.comment}");
    </#if>
  </#list>
<#else>
<#-- 未配置exportFields的情况下，按照默认配置 - 生成导出Excel字段 -->
        // 初始化导出字段
  <#list table.fields as field>
    <#if field.propertyName!=entityKeyName>
        EXPORT_EXCEL_COLUMN.put("${field.propertyName}", "${field.comment}");
    </#if>
  </#list>
</#if>

<#if importFields??>
<#-- 根据前端配置importFields - 生成导入Excel字段 -->
        // 初始化导入字段
  <#list table.fields as field>
    <#if importFields?seq_contains(field.annotationColumnName)>
        IMPORT_EXCEL_COLUMN.put("${field.comment}", "${field.propertyName}");
    </#if>
  </#list>
        // 初始化导入必填字段
  <#list table.fields as field>
    <#if importFields?seq_contains(field.annotationColumnName) && !field.metaInfo.nullable>
        IMPORT_REQUIRED_COLUMN.put("${field.propertyName}", "${field.comment}");
    </#if>
  </#list>
<#else>
<#-- 未配置importFields的情况下，按照默认配置 - 生成导入Excel字段 -->
        // 初始化导入字段
  <#list table.fields as field>
    <#if field.propertyName!=entityKeyName
        && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
        && field.propertyName!='createTime' && field.propertyName!='updateTime'>
        IMPORT_EXCEL_COLUMN.put("${field.comment}", "${field.propertyName}");
    </#if>
  </#list>
        // 初始化导入必填字段
  <#list table.fields as field>
    <#if field.propertyName!=entityKeyName && !field.metaInfo.nullable
        && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
        && field.propertyName!='createTime' && field.propertyName!='updateTime'>
        IMPORT_REQUIRED_COLUMN.put("${field.propertyName}", "${field.comment}");
    </#if>
  </#list>
</#if>
    }

}