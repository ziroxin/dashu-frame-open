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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模板路径配置项
 *
 * @author tzg hubin
 * @since 2017-06-17
 */
public class TemplateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConfig.class);

    /**
     * 设置实体模板路径
     */
    private String entity;

    /**
     * 设置实体模板路径(kotlin模板)
     */
    private String entityKt;

    /**
     * 设置DTO模板路径
     */
    private String dto;
    private String dtoconvert;

    /**
     * 设置Excel模板路径
     */
    private String excelConstant;
    private String excelOut;

    /**
     * 设置控制器模板路径
     */
    private String controller;

    /**
     * 设置indexVue模板路径
     */
    private String indexVue;

    /**
     * 设置permissionSQL模板路径
     */
    private String permissionSQL;

    /**
     * 设置Mapper模板路径
     */
    private String mapper;

    /**
     * 设置MapperXml模板路径
     */
    private String xml;

    /**
     * 设置Service模板路径
     */
    private String service;

    /**
     * 设置ServiceImpl模板路径
     */
    private String serviceImpl;

    /**
     * 是否禁用实体模板（默认 false）
     */
    private boolean disableEntity;

    /**
     * 不对外爆露
     */
    private TemplateConfig() {
        this.entity = ConstVal.TEMPLATE_ENTITY_JAVA;
        this.entityKt = ConstVal.TEMPLATE_ENTITY_KT;
        this.dto = ConstVal.TEMPLATE_DTO;
        this.dtoconvert = ConstVal.TEMPLATE_DTOCONVERT;
        this.excelConstant = ConstVal.TEMPLATE_EXCEL_CONSTANT;
        this.excelOut = ConstVal.TEMPLATE_EXCEL_OUT;
        this.controller = ConstVal.TEMPLATE_CONTROLLER;
        this.indexVue = ConstVal.TEMPLATE_INDEXVUE;
        this.permissionSQL = ConstVal.TEMPLATE_PERMISSION_SQL;
        this.mapper = ConstVal.TEMPLATE_MAPPER;
        this.xml = ConstVal.TEMPLATE_XML;
        this.service = ConstVal.TEMPLATE_SERVICE;
        this.serviceImpl = ConstVal.TEMPLATE_SERVICE_IMPL;
    }

    /**
     * 当模板赋值为空时进行日志提示打印
     *
     * @param value        模板值
     * @param templateType 模板类型
     */
    private void logger(String value, TemplateType templateType) {
        if (StringUtils.isBlank(value)) {
            LOGGER.warn("推荐使用disable(TemplateType.{})方法进行默认模板禁用.", templateType.name());
        }
    }

    /**
     * 获取实体模板路径
     *
     * @param kotlin 是否kotlin
     * @return 模板路径
     */
    public String getEntity(boolean kotlin) {
        if (!this.disableEntity) {
            if (kotlin) {
                return StringUtils.isBlank(this.entityKt) ? ConstVal.TEMPLATE_ENTITY_KT : this.entityKt;
            }
            return StringUtils.isBlank(this.entity) ? ConstVal.TEMPLATE_ENTITY_JAVA : this.entity;
        }
        return null;
    }

    /**
     * 禁用模板
     *
     * @param templateTypes 模板类型
     * @return this
     * @since 3.3.2
     */
    public TemplateConfig disable(@NotNull TemplateType... templateTypes) {
        if (templateTypes != null && templateTypes.length > 0) {
            for (TemplateType templateType : templateTypes) {
                switch (templateType) {
                    case ENTITY:
                        this.entity = null;
                        this.entityKt = null;
                        //暂时没其他多的需求,使用一个单独的boolean变量进行支持一下.
                        this.disableEntity = true;
                        break;
                    case DTO:
                        this.dto = null;
                        break;
                    case DTOCONVERT:
                        this.dtoconvert = null;
                        break;
                    case EXCEL_CONSTANT:
                        this.excelConstant = null;
                        break;
                    case EXCEL_OUT:
                        this.excelOut = null;
                        break;
                    case CONTROLLER:
                        this.controller = null;
                        break;
                    case INDEXVUE:
                        this.indexVue = null;
                        break;
                    case PERMISSIONSQL:
                        this.permissionSQL = null;
                        break;
                    case MAPPER:
                        this.mapper = null;
                        break;
                    case XML:
                        this.xml = null;
                        break;
                    case SERVICE:
                        this.service = null;
                        break;
                    case SERVICE_IMPL:
                        this.serviceImpl = null;
                        break;
                    default:
                }
            }
        }
        return this;
    }

    /**
     * 禁用全部模板
     *
     * @return this
     * @since 3.5.0
     */
    public TemplateConfig disable() {
        return disable(TemplateType.values());
    }

    public String getService() {
        return service;
    }

    public String getServiceImpl() {
        return serviceImpl;
    }

    public String getMapper() {
        return mapper;
    }

    public String getDTO() {
        return dto;
    }

    public String getDtoconvert() {
        return dtoconvert;
    }

    public String getExcelConstant() {
        return excelConstant;
    }

    public String getExcelOut() {
        return excelOut;
    }

    public String getXml() {
        return xml;
    }

    public String getController() {
        return controller;
    }

    public String getIndexVue() {
        return indexVue;
    }

    public String getPermissionSQL() {
        return permissionSQL;
    }

    /**
     * 模板路径配置构建者
     *
     * @author nieqiurong 3.5.0
     */
    public static class Builder implements IConfigBuilder<TemplateConfig> {

        private final TemplateConfig templateConfig;

        /**
         * 默认生成一个空的
         */
        public Builder() {
            this.templateConfig = new TemplateConfig();
        }

        /**
         * 禁用所有模板
         *
         * @return this
         */
        public Builder disable() {
            this.templateConfig.disable();
            return this;
        }

        /**
         * 禁用模板
         *
         * @return this
         */
        public Builder disable(@NotNull TemplateType... templateTypes) {
            this.templateConfig.disable(templateTypes);
            return this;
        }

        /**
         * 设置实体模板路径(JAVA)
         *
         * @param entityTemplate 实体模板
         * @return this
         */
        public Builder entity(@NotNull String entityTemplate) {
            this.templateConfig.disableEntity = false;
            this.templateConfig.entity = entityTemplate;
            return this;
        }

        /**
         * 设置实体模板路径(kotlin)
         *
         * @param entityKtTemplate 实体模板
         * @return this
         */
        public Builder entityKt(@NotNull String entityKtTemplate) {
            this.templateConfig.disableEntity = false;
            this.templateConfig.entityKt = entityKtTemplate;
            return this;
        }

        /**
         * 设置service模板路径
         *
         * @param serviceTemplate service接口模板路径
         * @return this
         */
        public Builder service(@NotNull String serviceTemplate) {
            this.templateConfig.service = serviceTemplate;
            return this;
        }

        /**
         * 设置serviceImpl模板路径
         *
         * @param serviceImplTemplate service实现类模板路径
         * @return this
         */
        public Builder serviceImpl(@NotNull String serviceImplTemplate) {
            this.templateConfig.serviceImpl = serviceImplTemplate;
            return this;
        }

        /**
         * 设置mapper模板路径
         *
         * @param mapperTemplate mapper模板路径
         * @return this
         */
        public Builder mapper(@NotNull String mapperTemplate) {
            this.templateConfig.mapper = mapperTemplate;
            return this;
        }

        /**
         * 设置mapperXml模板路径
         *
         * @param xmlTemplate xml模板路径
         * @return this
         */
        public Builder xml(@NotNull String xmlTemplate) {
            this.templateConfig.xml = xmlTemplate;
            return this;
        }

        /**
         * 设置控制器模板路径
         *
         * @param controllerTemplate 控制器模板路径
         * @return this
         */
        public Builder controller(@NotNull String controllerTemplate) {
            this.templateConfig.controller = controllerTemplate;
            return this;
        }

        /**
         * 设置DTO模板路径
         *
         * @param dtoTemplate DTO模板路径
         * @return this
         */
        public Builder DTO(@NotNull String dtoTemplate) {
            this.templateConfig.dto = dtoTemplate;
            return this;
        }

        /**
         * 设置ExcelConstant模板路径
         *
         * @param excelConstantTemplate excelConstant模板路径
         * @return this
         */
        public Builder excelConstant(@NotNull String excelConstantTemplate) {
            this.templateConfig.excelConstant = excelConstantTemplate;
            return this;
        }

        /**
         * 设置ExcelOut模板路径
         *
         * @param excelOutTemplate excelOut模板路径
         * @return this
         */
        public Builder excelOut(@NotNull String excelOutTemplate) {
            this.templateConfig.excelOut = excelOutTemplate;
            return this;
        }

        /**
         * 设置IndexVue模板路径
         *
         * @param indexVueTemplate IndexVue模板路径
         * @return this
         */
        public Builder indexVue(@NotNull String indexVueTemplate) {
            this.templateConfig.indexVue = indexVueTemplate;
            return this;
        }

        /**
         * 构建模板配置对象
         *
         * @return 模板配置对象
         */
        @Override
        public TemplateConfig build() {
            return this.templateConfig;
        }
    }
}
