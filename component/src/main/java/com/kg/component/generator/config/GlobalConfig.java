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

import com.kg.component.generator.config.rules.DateType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;


/**
 * 全局配置
 *
 * @author hubin
 * @since 2016-12-02
 */
public class GlobalConfig {

    private GlobalConfig() {
    }

    protected static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfig.class);

    /**
     * 生成文件的输出目录【 windows:D://  linux or mac:/tmp 】
     */
    private String outputDir = System.getProperty("os.name").toLowerCase().contains("windows") ? "D://" : "/tmp";

    /**
     * 是否打开输出目录
     */
    private boolean open = true;

    /**
     * 作者
     */
    private String author = "baomidou";

    /**
     * 开启 Kotlin 模式（默认 false）
     */
    private boolean kotlin;

    /**
     * 开启 swagger 模式（默认 false 与 springdoc 不可同时使用）
     */
    private boolean swagger;
    /**
     * 开启 springdoc 模式（默认 false 与 swagger 不可同时使用）
     */
    private boolean springdoc;

    /**
     * 时间类型对应策略
     */
    private DateType dateType = DateType.TIME_PACK;

    /**
     * 获取注释日期
     *
     * @since 3.5.0
     */
    private Supplier<String> commentDate = () -> new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    public String getOutputDir() {
        return outputDir;
    }

    public boolean isOpen() {
        return open;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isKotlin() {
        return kotlin;
    }

    public boolean isSwagger() {
        // springdoc 设置优先于 swagger
        return springdoc ? false : swagger;
    }

    public boolean isSpringdoc() {
        return springdoc;
    }

    @NotNull
    public DateType getDateType() {
        return dateType;
    }

    @NotNull
    public String getCommentDate() {
        return commentDate.get();
    }

    /**
     * 全局配置构建
     *
     * @author nieqiurong 2020/10/11.
     * @since 3.5.0
     */
    public static class Builder implements IConfigBuilder<GlobalConfig> {

        private final GlobalConfig globalConfig;

        public Builder() {
            this.globalConfig = new GlobalConfig();
        }

        /**
         * 禁止打开输出目录
         */
        public Builder disableOpenDir() {
            this.globalConfig.open = false;
            return this;
        }

        /**
         * 输出目录
         */
        public Builder outputDir(@NotNull String outputDir) {
            this.globalConfig.outputDir = outputDir;
            return this;
        }

        /**
         * 作者
         */
        public Builder author(@NotNull String author) {
            this.globalConfig.author = author;
            return this;
        }

        /**
         * 开启 kotlin 模式
         */
        public Builder enableKotlin() {
            this.globalConfig.kotlin = true;
            return this;
        }

        /**
         * 开启 swagger 模式
         */
        public Builder enableSwagger() {
            this.globalConfig.swagger = true;
            return this;
        }

        /**
         * 开启 springdoc 模式
         */
        public Builder enableSpringdoc() {
            this.globalConfig.springdoc = true;
            return this;
        }

        /**
         * 时间类型对应策略
         */
        public Builder dateType(@NotNull DateType dateType) {
            this.globalConfig.dateType = dateType;
            return this;
        }

        /**
         * 注释日期获取处理
         * example: () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
         *
         * @param commentDate 获取注释日期
         * @return this
         * @since 3.5.0
         */
        public Builder commentDate(@NotNull Supplier<String> commentDate) {
            this.globalConfig.commentDate = commentDate;
            return this;
        }

        /**
         * 指定注释日期格式化
         *
         * @param pattern 格式
         * @return this
         * @since 3.5.0
         */
        public Builder commentDate(@NotNull String pattern) {
            return commentDate(() -> new SimpleDateFormat(pattern).format(new Date()));
        }

        @Override
        public GlobalConfig build() {
            return this.globalConfig;
        }
    }
}
