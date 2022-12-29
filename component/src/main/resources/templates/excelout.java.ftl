package ${package.ExcelOut};

<#list table.importPackages as pkg>
    <#if !pkg?string?contains('com.baomidou.mybatisplus.annotation.') && !pkg?string?contains('BaseEntity')>
import ${pkg};
    </#if>
</#list>

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - ${table.comment!}
 *
 * @author ${author}
 * @date ${date}
 */
@Getter
@Setter
public class ${entity}ExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

<#list table.fields as field>

    /**
     * ${field.comment}
     */
	<#if field.propertyType=="LocalDateTime">
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	</#if>
	<#if field.propertyType=="Date" || field.propertyType=="LocalDate">
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
	</#if>
    private ${field.propertyType} ${field.propertyName};
</#list>

}