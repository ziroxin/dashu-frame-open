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

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.kg.component.generator.config.DataSourceConfig;
import com.kg.component.generator.config.GlobalConfig;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.builder.ConfigBuilder;
import com.kg.component.generator.config.po.TableInfo;
import com.kg.component.generator.config.querys.DbQueryDecorator;
import com.kg.component.generator.jdbc.DatabaseMetaDataWrapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库查询抽象类
 *
 * @author nieqiurong
 * @since 3.5.3
 */
public abstract class AbstractDatabaseQuery implements IDatabaseQuery {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected final ConfigBuilder configBuilder;

    protected final DataSourceConfig dataSourceConfig;

    protected final StrategyConfig strategyConfig;

    protected final GlobalConfig globalConfig;

    protected final DbQueryDecorator dbQuery;

    protected final DatabaseMetaDataWrapper databaseMetaDataWrapper;

    public AbstractDatabaseQuery(@NotNull ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        this.dataSourceConfig = configBuilder.getDataSourceConfig();
        this.strategyConfig = configBuilder.getStrategyConfig();
        this.dbQuery = new DbQueryDecorator(dataSourceConfig, strategyConfig);
        this.globalConfig = configBuilder.getGlobalConfig();
        this.databaseMetaDataWrapper = new DatabaseMetaDataWrapper(dataSourceConfig);
    }

    @NotNull
    public ConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    @NotNull
    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    protected void filter(List<TableInfo> tableList, List<TableInfo> includeTableList, List<TableInfo> excludeTableList) {
        boolean isInclude = strategyConfig.getInclude().size() > 0;
        boolean isExclude = strategyConfig.getExclude().size() > 0;
        if (isExclude || isInclude) {
            Map<String, String> notExistTables = new HashSet<>(isExclude ? strategyConfig.getExclude() : strategyConfig.getInclude())
                .stream()
                .filter(s -> !ConfigBuilder.matcherRegTable(s))
                .collect(Collectors.toMap(String::toLowerCase, s -> s, (o, n) -> n));
            // 将已经存在的表移除，获取配置中数据库不存在的表
            for (TableInfo tabInfo : tableList) {
                if (notExistTables.isEmpty()) {
                    break;
                }
                //解决可能大小写不敏感的情况导致无法移除掉
                notExistTables.remove(tabInfo.getName().toLowerCase());
            }
            if (notExistTables.size() > 0) {
                LOGGER.warn("表[{}]在数据库中不存在！！！", String.join(StringPool.COMMA, notExistTables.values()));
            }
            // 需要反向生成的表信息
            if (isExclude) {
                tableList.removeAll(excludeTableList);
            } else {
                tableList.clear();
                tableList.addAll(includeTableList);
            }
        }
    }
}
