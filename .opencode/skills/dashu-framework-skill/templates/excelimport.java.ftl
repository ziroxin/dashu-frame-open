package ${package.ExcelOut};

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导入Excel实体 - ${table.comment!}
 *
 * @author ${author}
 * @date ${date}
 */
@Getter
@Setter
public class ${entity}ExcelImportDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;
<#if importFields??>
<#-- 根据前端配置importFields - 生成导入Excel字段 -->
  <#list table.fields as field>
    <#if importFields?seq_contains(field.annotationColumnName)>

    /** ${field.comment} */
    private String ${field.propertyName};
    </#if>
  </#list>
<#else>
<#-- 未配置importFields - 导入默认字段 -->
  <#list table.fields as field>
    <#if field.propertyName!=entityKeyName
            && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
            && field.propertyName!='createTime' && field.propertyName!='updateTime'>

    /** ${field.comment} */
    private String ${field.propertyName};
    </#if>
  </#list>
</#if>
}