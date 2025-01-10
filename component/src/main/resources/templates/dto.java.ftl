package ${package.DTO};

import com.fasterxml.jackson.annotation.JsonFormat;
<#list table.importPackages as pkg>
    <#if !pkg?string?contains('com.baomidou.mybatisplus.annotation.') && !pkg?string?contains('BaseEntity')>
import ${pkg};
    </#if>
</#list>
<#if springdoc>
import io.swagger.v3.oas.annotations.media.Schema;
<#elseif swagger>
<#if superDTOClassPackage??>
import ${superDTOClassPackage};
</#if>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
</#if>
<#if dtoLombokModel>
import lombok.Getter;
import lombok.Setter;

    <#if chainModel>
import lombok.experimental.Accessors;
    </#if>
</#if>
<#if childTableList??>
    <#list childTableList as child>
import ${packageBaseParent}.${child}.entity.${child?cap_first};
    </#list>
import java.util.List;
</#if>

/**
 * <p>
 * ${table.comment!}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if dtoLombokModel>
@Getter
@Setter
    <#if chainModel>
@Accessors(chain = true)
    </#if>
</#if>
<#if springdoc>
@Schema(name = "${dtoName}", description = "$!{table.comment}")
<#elseif swagger>
@ApiModel(value = "${dtoName}", description = "${table.comment!}")
</#if>
<#if superDTOClass??>
public class ${dtoName} implements ${superDTOClass}<#if activeRecord><${dtoName}></#if> {
<#elseif activeRecord>
public class ${dtoName} extends Model<${dtoName}> {
<#elseif dtoSerialVersionUID>
public class ${dtoName} implements Serializable {
<#else>
public class ${dtoName} {
</#if>
<#if dtoSerialVersionUID>
    private static final long serialVersionUID = 1L;
</#if>
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>

    <#if field.comment!?length gt 0>
        <#if springdoc>
    @Schema(description = "${field.comment}")
        <#elseif swagger>
    @ApiModelProperty("${field.comment}")
        <#else>
    /**
     * ${field.comment}
     */
        </#if>
    </#if>
    <#if field.propertyType=="LocalDateTime">
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    </#if>
    <#if field.propertyType=="Date" || field.propertyType=="LocalDate">
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>
<#if childTableList??>
    <#list childTableList as child>

    private List<${child?cap_first}> ${child?lower_case}List;
	</#list>
</#if>
<#------------  END 字段循环遍历  ---------->
<#if !dtoLombokModel>
    <#list table.fields as field>
        <#if field.propertyType == "boolean">
            <#assign getprefix="is"/>
        <#else>
            <#assign getprefix="get"/>
        </#if>

    public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }

    <#if chainModel>
    public ${dtoName} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
    <#else>
    public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
    </#if>
        this.${field.propertyName} = ${field.propertyName};
        <#if chainModel>
        return this;
        </#if>
    }
    </#list>
</#if>
<#if dtoColumnConstant>
    <#list table.fields as field>

    public static final String ${field.name?upper_case} = "${field.name}";
    </#list>
</#if>
<#if activeRecord>

    @Override
    public Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }
</#if>
<#if !dtoLombokModel>

    @Override
    public String toString() {
        return "${dtoName}{" +
    <#list table.fields as field>
        <#if field_index==0>
            "${field.propertyName} = " + ${field.propertyName} +
        <#else>
            ", ${field.propertyName} = " + ${field.propertyName} +
        </#if>
    </#list>
        "}";
    }
</#if>
}
