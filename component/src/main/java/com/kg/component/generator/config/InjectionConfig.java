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
package com.kg.component.generator.config;

import com.kg.component.generator.config.builder.CustomFile;
import com.kg.component.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 注入配置
 *
 * @author hubin
 * @since 2016-12-07
 */
public class InjectionConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(InjectionConfig.class);

    /**
     * 输出文件之前消费者
     */
    private BiConsumer<TableInfo, Map<String, Object>> beforeOutputFileBiConsumer;

    /**
     * 自定义配置 Map 对象
     */
    private Map<String, Object> customMap = new HashMap<>();

    /**
     * 自定义模板文件，key为文件名称，value为模板路径（已弃用，换成了customFiles，3.5.4版本会删除此方法）
     */
    @Deprecated
    private Map<String, String> customFile = new HashMap<>();

    /**
     * 自定义模板文件列表
     *
     * @since 3.5.3
     */
    private List<CustomFile> customFiles = new ArrayList<>();

    /**
     * 输出文件前
     */
    @NotNull
    public void beforeOutputFile(TableInfo tableInfo, Map<String, Object> objectMap) {
        if (!customMap.isEmpty()) {
            objectMap.putAll(customMap);
        }
        if (null != beforeOutputFileBiConsumer) {
            beforeOutputFileBiConsumer.accept(tableInfo, objectMap);
        }
    }

    /**
     * 获取自定义配置 Map 对象
     */
    @NotNull
    public Map<String, Object> getCustomMap() {
        return customMap;
    }

    /**
     * 已弃用，换成了customFiles，3.5.4版本会删除此方法
     */
    @NotNull
    @Deprecated
    public Map<String, String> getCustomFile() {
        return customFile;
    }

    /**
     * 获取自定义模板文件列表
     */
    @NotNull
    public List<CustomFile> getCustomFiles() {
        return customFiles;
    }

    /**
     * 构建者
     */
    public static class Builder implements IConfigBuilder<InjectionConfig> {

        private final InjectionConfig injectionConfig;

        public Builder() {
            this.injectionConfig = new InjectionConfig();
        }

        /**
         * 输出文件之前消费者
         *
         * @param biConsumer 消费者
         * @return this
         */
        public Builder beforeOutputFile(@NotNull BiConsumer<TableInfo, Map<String, Object>> biConsumer) {
            this.injectionConfig.beforeOutputFileBiConsumer = biConsumer;
            return this;
        }

        /**
         * 自定义配置 Map 对象
         *
         * @param customMap Map 对象
         * @return this
         */
        public Builder customMap(@NotNull Map<String, Object> customMap) {
            this.injectionConfig.customMap = customMap;
            return this;
        }

        /**
         * 自定义配置模板文件
         *
         * @param customFile key为文件名称，value为文件路径
         * @return this
         */
        public Builder customFile(@NotNull Map<String, String> customFile) {
            return customFile(customFile.entrySet().stream()
                .map(e -> new CustomFile.Builder().fileName(e.getKey()).templatePath(e.getValue()).build())
                .collect(Collectors.toList()));
        }

        public Builder customFile(@NotNull CustomFile customFile) {
            this.injectionConfig.customFiles.add(customFile);
            return this;
        }

        public Builder customFile(@NotNull List<CustomFile> customFiles) {
            this.injectionConfig.customFiles.addAll(customFiles);
            return this;
        }

        public Builder customFile(Consumer<CustomFile.Builder> consumer) {
            CustomFile.Builder builder = new CustomFile.Builder();
            consumer.accept(builder);
            this.injectionConfig.customFiles.add(builder.build());
            return this;
        }

        @Override
        public InjectionConfig build() {
            return this.injectionConfig;
        }
    }
}
