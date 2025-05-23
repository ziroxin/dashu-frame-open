/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kg.component.generator.query;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kg.component.generator.config.DataSourceConfig;
import com.kg.component.generator.config.builder.ConfigBuilder;
import com.kg.component.generator.config.builder.Entity;
import com.kg.component.generator.config.po.TableField;
import com.kg.component.generator.config.po.TableInfo;
import com.kg.component.generator.config.rules.IColumnType;
import com.kg.component.generator.jdbc.DatabaseMetaDataWrapper;
import com.kg.component.generator.type.ITypeConvertHandler;
import com.kg.component.generator.type.TypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 元数据查询数据库信息.
 *
 * @author nieqiurong 2022/5/11.
 * @see ITypeConvertHandler 类型转换器(如果默认逻辑不能满足，可实现此接口重写类型转换)
 * <p>
 * 测试通过的数据库：H2、Mysql-5.7.37、Mysql-8.0.25、PostgreSQL-11.15、PostgreSQL-14.1、Oracle-11.2.0.1.0、DM8
 * </p>
 * <p>
 * FAQ:
 * 1.Mysql无法读取表注释: 链接增加属性 remarks=true&useInformationSchema=true 或者通过{@link DataSourceConfig.Builder#addConnectionProperty(String, String)}设置
 * 2.Oracle无法读取注释: 增加属性remarks=true，也有些驱动版本说是增加remarksReporting=true {@link DataSourceConfig.Builder#addConnectionProperty(String, String)}
 * </p>
 * @since 3.5.3
 */
public class DefaultQuery extends AbstractDatabaseQuery {

    private final TypeRegistry typeRegistry;

    public DefaultQuery(@NotNull ConfigBuilder configBuilder) {
        super(configBuilder);
        typeRegistry = new TypeRegistry(configBuilder.getGlobalConfig());
    }

    @Override
    public @NotNull List<TableInfo> queryTables() {
        boolean isInclude = strategyConfig.getInclude().size() > 0;
        boolean isExclude = strategyConfig.getExclude().size() > 0;
        //所有的表信息
        List<TableInfo> tableList = new ArrayList<>();
        List<DatabaseMetaDataWrapper.Table> tables = getTables();
        //需要反向生成或排除的表信息
        List<TableInfo> includeTableList = new ArrayList<>();
        List<TableInfo> excludeTableList = new ArrayList<>();
        tables.forEach(table -> {
            String tableName = table.getName();
            if (StringUtils.isNotBlank(tableName)) {
                TableInfo tableInfo = new TableInfo(this.configBuilder, tableName);
                tableInfo.setComment(table.getRemarks());
                if (isInclude && strategyConfig.matchIncludeTable(tableName)) {
                    includeTableList.add(tableInfo);
                } else if (isExclude && strategyConfig.matchExcludeTable(tableName)) {
                    excludeTableList.add(tableInfo);
                }
                tableList.add(tableInfo);
            }
        });
        filter(tableList, includeTableList, excludeTableList);
        // 性能优化，只处理需执行表字段 https://github.com/baomidou/mybatis-plus/issues/219
        tableList.forEach(this::convertTableFields);
        return tableList;
    }

    protected List<DatabaseMetaDataWrapper.Table> getTables() {
        boolean skipView = strategyConfig.isSkipView();
        // 暂时采取内存过滤，还需要找数据库测试schema
        return databaseMetaDataWrapper.getTables(null, skipView ? new String[]{"TABLE"} : new String[]{"TABLE", "VIEW"});
    }

    protected void convertTableFields(@NotNull TableInfo tableInfo) {
        String tableName = tableInfo.getName();
        Map<String, DatabaseMetaDataWrapper.Column> columnsInfoMap = getColumnsInfo(tableName);
        Entity entity = strategyConfig.entity();
        columnsInfoMap.forEach((k, columnInfo) -> {
            TableField.MetaInfo metaInfo = new TableField.MetaInfo(columnInfo);
            String columnName = columnInfo.getName();
            TableField field = new TableField(this.configBuilder, columnName);
            // 处理ID
            if (columnInfo.isPrimaryKey()) {
                field.primaryKey(columnInfo.isAutoIncrement());
                tableInfo.setHavePrimaryKey(true);
                if (field.isKeyIdentityFlag() && entity.getIdType() != null) {
                    LOGGER.warn("当前表[{}]的主键为自增主键，会导致全局主键的ID类型设置失效!", tableName);
                }
            }
            field.setColumnName(columnName).setComment(columnInfo.getRemarks());
            String propertyName = entity.getNameConvert().propertyNameConvert(field);
            IColumnType columnType = typeRegistry.getColumnType(metaInfo);
            ITypeConvertHandler typeConvertHandler = dataSourceConfig.getTypeConvertHandler();
            if (typeConvertHandler != null) {
                columnType = typeConvertHandler.convert(globalConfig, typeRegistry, metaInfo);
            }
            field.setPropertyName(propertyName, columnType);
            field.setMetaInfo(metaInfo);
            tableInfo.addField(field);
        });
        tableInfo.processTable();
    }

    protected Map<String, DatabaseMetaDataWrapper.Column> getColumnsInfo(String tableName) {
        return databaseMetaDataWrapper.getColumnsInfo(tableName, true);
    }
}
