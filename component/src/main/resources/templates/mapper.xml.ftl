<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">
<#if enableCache>
    <!-- 开启二级缓存 -->
    <cache type="${cacheClassName}"/>
</#if>
<#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
<#list table.fields as field>
<#if field.keyFlag><#--生成主键排在第一位-->
        <id column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
<#list table.commonFields as field><#--生成公共字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
</#list>
<#list table.fields as field>
<#if !field.keyFlag><#--生成普通字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
    </resultMap>
</#if>
<#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
<#list table.commonFields as field>
        ${field.columnName},
</#list>
        ${table.fieldNames}
    </sql>
</#if>
    <!-- 批量保存 -->
    <insert id="saveList">
        INSERT INTO ${table.name} VALUES
        <foreach collection="list" item="row" separator=",">
            (
<#list table.fields as field>
            ${'#'}{row.${field.propertyName}}<#if field_has_next>,</#if>
</#list>
            )
        </foreach>
    </insert>
    <!-- 根据条件查询列表 -->
    <select id="list" resultType="${package.DTO}.${dtoName}">
        SELECT * FROM ${table.name}
        WHERE 1=1
<#list table.fields as field>
    <#if field.propertyName!=entityKeyName && field.propertyName!='orderIndex' && field.propertyName!='createTime' && field.propertyName!='updateTime'>
        <if test="${field.propertyName}!= null and ${field.propertyName}!= ''">
            AND ${field.annotationColumnName} = ${'#{'+field.propertyName+'}'}
        </if>
    </#if>
</#list>
<#list table.fields as field>
    <#if field.propertyName=='orderIndex'>
        ORDER BY order_index ASC
    </#if>
</#list>
        <if test="offset != null and limit != null">
            LIMIT ${'#'}{offset}, ${'#'}{limit}
        </if>
    </select>
    <!-- 根据条件查询总数 -->
    <select id="count" resultType="java.lang.Long">
        SELECT COUNT(*) FROM ${table.name}
        WHERE 1=1
<#list table.fields as field>
    <#if field.propertyName!=entityKeyName && field.propertyName!='orderIndex' && field.propertyName!='createTime' && field.propertyName!='updateTime'>
        <if test="${field.propertyName}!= null and ${field.propertyName}!= ''">
            AND ${field.annotationColumnName} = ${'#{'+field.propertyName+'}'}
        </if>
    </#if>
</#list>
    </select>
</mapper>
