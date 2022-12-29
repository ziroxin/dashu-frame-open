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

    static {
        // 初始化导出字段
<#list table.fields as field>
        EXPORT_EXCEL_COLUMN.put("${field.propertyName}", "${field.comment}");
</#list>
    }

}