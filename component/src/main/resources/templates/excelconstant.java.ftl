package ${package.ExcelConstant};

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public static Map<String, String> IMPORT_EXCEL_COLUMN = new HashMap<>();

    static {
        // 初始化导出字段
<#list table.fields as field>
        EXPORT_EXCEL_COLUMN.put("${field.propertyName}", "${field.comment}");
</#list>
        // 初始化导入字段
<#list table.fields as field>
        IMPORT_EXCEL_COLUMN.put("${field.comment}", "${field.propertyName}");
</#list>
    }

}