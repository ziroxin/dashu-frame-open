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
        // 初始化导出字段
<#list table.fields as field>
    <#if field.propertyName!=entityKeyName>
        EXPORT_EXCEL_COLUMN.put("${field.propertyName}", "${field.comment}");
    </#if>
</#list>
        // 初始化导入字段
<#list table.fields as field>
    <#if field.propertyName!=entityKeyName && field.propertyName!='createTime' && field.propertyName!='updateTime'>
        IMPORT_EXCEL_COLUMN.put("${field.comment}", "${field.propertyName}");
    </#if>
</#list>
        // 初始化导入必填字段
<#list table.fields as field>
    <#if field.propertyName!=entityKeyName && field.propertyName!='createTime' && field.propertyName!='updateTime' && !field.metaInfo.nullable>
        IMPORT_REQUIRED_COLUMN.put("${field.propertyName}", "${field.comment}");
    </#if>
</#list>
    }

}