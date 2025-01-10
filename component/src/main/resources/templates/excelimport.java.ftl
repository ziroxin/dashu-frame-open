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
<#list table.fields as field>
  <#if field.propertyName!=entityKeyName
          && field.propertyName!='createUserId' && field.propertyName!='updateUserId'
          && field.propertyName!='createTime' && field.propertyName!='updateTime'>

    /** ${field.comment} */
    private String ${field.propertyName};
  </#if>
</#list>
}