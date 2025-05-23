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
package com.kg.component.generator.engine;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kg.component.generator.config.*;
import com.kg.component.generator.config.builder.ConfigBuilder;
import com.kg.component.generator.config.builder.CustomFile;
import com.kg.component.generator.config.po.TableField;
import com.kg.component.generator.config.po.TableInfo;
import com.kg.component.generator.util.FileUtils;
import com.kg.component.generator.util.RuntimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


/**
 * 模板引擎抽象类
 *
 * @author hubin
 * @since 2018-01-10
 */
public abstract class AbstractTemplateEngine {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 配置信息
     */
    private ConfigBuilder configBuilder;

    /**
     * 模板引擎初始化
     */
    @NotNull
    public abstract AbstractTemplateEngine init(@NotNull ConfigBuilder configBuilder);

    /**
     * 输出自定义模板文件
     *
     * @param customFiles 自定义模板文件列表
     * @param tableInfo   表信息
     * @param objectMap   渲染数据
     * @since 3.5.3
     */
    protected void outputCustomFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        String entityName = tableInfo.getEntityName();
        String parentPath = getPathInfo(OutputFile.parent);
        customFiles.forEach(file -> {
            String filePath = StringUtils.isNotBlank(file.getFilePath()) ? file.getFilePath() : parentPath;
            if (StringUtils.isNotBlank(file.getPackageName())) {
                filePath = filePath + File.separator + file.getPackageName();
                filePath = filePath.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
            }
            String fileName = filePath + File.separator + entityName + file.getFileName();
            outputFile(new File(fileName), objectMap, file.getTemplatePath(), file.isFileOverride());
        });
    }

    /**
     * 输出实体文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputEntity(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        String entityName = tableInfo.getEntityName();
        String entityPath = getPathInfo(OutputFile.entity);
        if (StringUtils.isNotBlank(entityName) && StringUtils.isNotBlank(entityPath)) {
            getTemplateFilePath(template -> template.getEntity(getConfigBuilder().getGlobalConfig().isKotlin())).ifPresent((entity) -> {
                String entityFile = String.format((entityPath + File.separator + "%s" + suffixJavaOrKt()), entityName);
                outputFile(new File(entityFile), objectMap, entity, getConfigBuilder().getStrategyConfig().entity().isFileOverride());
            });
        }
    }

    /**
     * 输出DTO文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputDTO(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // DTO.java
        String entityName = tableInfo.getEntityName();
        String dtoPath = getPathInfo(OutputFile.dto);
        if (StringUtils.isNotBlank(tableInfo.getDTOName()) && StringUtils.isNotBlank(dtoPath)) {
            getTemplateFilePath(TemplateConfig::getDTO).ifPresent(dto -> {
                String dtoFile = String.format((dtoPath + File.separator + tableInfo.getDTOName() + suffixJavaOrKt()), entityName);
                outputFile(new File(dtoFile), objectMap, dto, getConfigBuilder().getStrategyConfig().dto().isFileOverride());
            });
        }
        // Convert.java
        String dtoconvertPath = getPathInfo(OutputFile.convert);
        if (StringUtils.isNotBlank(tableInfo.getDtoconvertName()) && StringUtils.isNotBlank(dtoconvertPath)) {
            getTemplateFilePath(TemplateConfig::getDtoconvert).ifPresent(convert -> {
                String dtoconvertFile = String.format((dtoconvertPath + File.separator + tableInfo.getDtoconvertName() + suffixJavaOrKt()), entityName);
                outputFile(new File(dtoconvertFile), objectMap, convert, getConfigBuilder().getStrategyConfig().dto().isFileOverride());
            });
        }
    }

    /**
     * 输出Excels相关文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputExcels(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // ExcelConstant.java
        String entityName = tableInfo.getEntityName();
        String excelConstantPath = getPathInfo(OutputFile.excelConstant);
        if (StringUtils.isNotBlank(excelConstantPath)) {
            getTemplateFilePath(TemplateConfig::getExcelConstant).ifPresent(constant -> {
                String excelConstantFile = excelConstantPath + File.separator + entityName + "ExcelConstant" + suffixJavaOrKt();
                outputFile(new File(excelConstantFile), objectMap, constant, getConfigBuilder().getStrategyConfig().excels().isFileOverride());
            });
        }
        // ExcelOut.java
        String excelOutPath = getPathInfo(OutputFile.excelOut);
        if (StringUtils.isNotBlank(excelOutPath)) {
            getTemplateFilePath(TemplateConfig::getExcelOut).ifPresent(out -> {
                String excelOutFile = excelOutPath + File.separator + entityName + "ExcelOutDTO" + suffixJavaOrKt();
                outputFile(new File(excelOutFile), objectMap, out, getConfigBuilder().getStrategyConfig().excels().isFileOverride());
            });
        }
        // ExcelImport.java
        String excelImportPath = getPathInfo(OutputFile.excelImport);
        if (StringUtils.isNotBlank(excelImportPath)) {
            getTemplateFilePath(TemplateConfig::getExcelImport).ifPresent(importer -> {
                String excelImportFile = excelImportPath + File.separator + entityName + "ExcelImportDTO" + suffixJavaOrKt();
                outputFile(new File(excelImportFile), objectMap, importer, getConfigBuilder().getStrategyConfig().excels().isFileOverride());
            });
        }
    }

    /**
     * 输出Mapper文件(含xml)
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputMapper(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // MpMapper.java
        String entityName = tableInfo.getEntityName();
        String mapperPath = getPathInfo(OutputFile.mapper);
        if (StringUtils.isNotBlank(tableInfo.getMapperName()) && StringUtils.isNotBlank(mapperPath)) {
            getTemplateFilePath(TemplateConfig::getMapper).ifPresent(mapper -> {
                String mapperFile = String.format((mapperPath + File.separator + tableInfo.getMapperName() + suffixJavaOrKt()), entityName);
                outputFile(new File(mapperFile), objectMap, mapper, getConfigBuilder().getStrategyConfig().mapper().isFileOverride());
            });
        }
        // MpMapper.xml
        String xmlPath = getPathInfo(OutputFile.xml);
        if (StringUtils.isNotBlank(tableInfo.getXmlName()) && StringUtils.isNotBlank(xmlPath)) {
            getTemplateFilePath(TemplateConfig::getXml).ifPresent(xml -> {
                String xmlFile = String.format((xmlPath + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
                outputFile(new File(xmlFile), objectMap, xml, getConfigBuilder().getStrategyConfig().mapper().isFileOverride());
            });
        }
    }

    /**
     * 输出service文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputService(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // MpService.java
        String entityName = tableInfo.getEntityName();
        String servicePath = getPathInfo(OutputFile.service);
        if (StringUtils.isNotBlank(tableInfo.getServiceName()) && StringUtils.isNotBlank(servicePath)) {
            getTemplateFilePath(TemplateConfig::getService).ifPresent(service -> {
                String serviceFile = String.format((servicePath + File.separator + tableInfo.getServiceName() + suffixJavaOrKt()), entityName);
                outputFile(new File(serviceFile), objectMap, service, getConfigBuilder().getStrategyConfig().service().isFileOverride());
            });
        }
        // MpServiceImpl.java
        String serviceImplPath = getPathInfo(OutputFile.serviceImpl);
        if (StringUtils.isNotBlank(tableInfo.getServiceImplName()) && StringUtils.isNotBlank(serviceImplPath)) {
            getTemplateFilePath(TemplateConfig::getServiceImpl).ifPresent(serviceImpl -> {
                String implFile = String.format((serviceImplPath + File.separator + tableInfo.getServiceImplName() + suffixJavaOrKt()), entityName);
                outputFile(new File(implFile), objectMap, serviceImpl, getConfigBuilder().getStrategyConfig().service().isFileOverride());
            });
        }
    }

    /**
     * 输出controller文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputController(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // MpController.java
        String controllerPath = getPathInfo(OutputFile.controller);
        if (StringUtils.isNotBlank(tableInfo.getControllerName()) && StringUtils.isNotBlank(controllerPath)) {
            getTemplateFilePath(TemplateConfig::getController).ifPresent(controller -> {
                String entityName = tableInfo.getEntityName();
                String controllerFile = String.format((controllerPath + File.separator + tableInfo.getControllerName() + suffixJavaOrKt()), entityName);
                outputFile(new File(controllerFile), objectMap, controller, getConfigBuilder().getStrategyConfig().controller().isFileOverride());
            });
        }
    }

    /**
     * 输出indexVue文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputIndexVue(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // mp/index.vue
        String indexVuePath = getPathInfo(OutputFile.indexVue);
        if (StringUtils.isNotBlank(indexVuePath)) {
            getTemplateFilePath(TemplateConfig::getIndexVue).ifPresent(indexVue -> {
                String indexVueFile = indexVuePath + File.separator + objectMap.get("indexVuePackage") + File.separator + "index" + ConstVal.VUE_SUFFIX;
                outputFile(new File(indexVueFile), objectMap, indexVue, getConfigBuilder().getStrategyConfig().indexVue().isFileOverride());
            });
        }
    }

    /**
     * 输出permissionSQL文件
     *
     * @param tableInfo 表信息
     * @param objectMap 渲染数据
     * @since 3.5.0
     */
    protected void outputPermissionSQL(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // mp/index.vue
        String permissionSQLPath = getPathInfo(OutputFile.permissionSql);
        if (StringUtils.isNotBlank(permissionSQLPath)) {
            getTemplateFilePath(TemplateConfig::getPermissionSQL).ifPresent(permissionSQL -> {
                String entityName = tableInfo.getEntityName();
                String permissionSQLFile = permissionSQLPath + File.separator + entityName + "-permission" + ConstVal.SQL_SUFFIX;
                outputFile(new File(permissionSQLFile), objectMap, permissionSQL, getConfigBuilder().getStrategyConfig().permissionSQL().isFileOverride());
            });
        }
    }

    /**
     * 输出文件（3.5.4版本会删除此方法）
     *
     * @param file         文件
     * @param objectMap    渲染信息
     * @param templatePath 模板路径
     * @since 3.5.0
     */
    @Deprecated
    protected void outputFile(@NotNull File file, @NotNull Map<String, Object> objectMap, @NotNull String templatePath) {
        outputFile(file, objectMap, templatePath, false);
    }

    /**
     * 输出文件
     *
     * @param file         文件
     * @param objectMap    渲染信息
     * @param templatePath 模板路径
     * @param fileOverride 是否覆盖已有文件
     * @since 3.5.2
     */
    protected void outputFile(@NotNull File file, @NotNull Map<String, Object> objectMap, @NotNull String templatePath, boolean fileOverride) {
        if (isCreate(file, fileOverride)) {
            try {
                // 全局判断【默认】
                boolean exist = file.exists();
                if (!exist) {
                    File parentFile = file.getParentFile();
                    FileUtils.forceMkdir(parentFile);
                }
                writer(objectMap, templatePath, file);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * 获取模板路径
     *
     * @param function function
     * @return 模板路径
     * @since 3.5.0
     */
    @NotNull
    protected Optional<String> getTemplateFilePath(@NotNull Function<TemplateConfig, String> function) {
        TemplateConfig templateConfig = getConfigBuilder().getTemplateConfig();
        String filePath = function.apply(templateConfig);
        if (StringUtils.isNotBlank(filePath)) {
            return Optional.of(templateFilePath(filePath));
        }
        return Optional.empty();
    }

    /**
     * 获取路径信息
     *
     * @param outputFile 输出文件
     * @return 路径信息
     */
    @Nullable
    protected String getPathInfo(@NotNull OutputFile outputFile) {
        return getConfigBuilder().getPathInfo().get(outputFile);
    }

    /**
     * 批量输出 java xml 文件
     */
    @NotNull
    public AbstractTemplateEngine batchOutput() {
        try {
            ConfigBuilder config = this.getConfigBuilder();
            List<TableInfo> tableInfoList = config.getTableInfoList();
            tableInfoList.forEach(tableInfo -> {
                Map<String, Object> objectMap = this.getObjectMap(config, tableInfo);
                Optional.ofNullable(config.getInjectionConfig()).ifPresent(t -> {
                    // 添加自定义属性
                    t.beforeOutputFile(tableInfo, objectMap);
                    // 输出自定义文件
                    outputCustomFile(t.getCustomFiles(), tableInfo, objectMap);
                });
                // entity
                outputEntity(tableInfo, objectMap);
                // dto
                outputDTO(tableInfo, objectMap);
                // excel相关文件
                outputExcels(tableInfo, objectMap);
                // mapper and xml
                outputMapper(tableInfo, objectMap);
                // service
                outputService(tableInfo, objectMap);
                // controller
                outputController(tableInfo, objectMap);

                // vue路径未配置时，不生成前端vue和权限sql
                if (objectMap.get("permissionRouter") != null) {
                    // indexVue
                    outputIndexVue(tableInfo, objectMap);
                    // permissionSQL
                    outputPermissionSQL(tableInfo, objectMap);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("无法创建文件，请检查配置信息！", e);
        }
        return this;
    }

    /**
     * 输出文件（3.5.4版本会删除此方法）
     *
     * @param objectMap    渲染数据
     * @param templatePath 模板路径
     * @param outputFile   输出文件
     * @throws Exception ex
     * @deprecated 3.5.0
     */
    @Deprecated
    protected void writerFile(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        if (StringUtils.isNotBlank(templatePath)) {
            this.writer(objectMap, templatePath, outputFile);
        }
    }

    /**
     * 将模板转化成为文件（3.5.4版本会删除此方法）
     *
     * @param objectMap    渲染对象 MAP 信息
     * @param templatePath 模板文件
     * @param outputFile   文件生成的目录
     * @see #writer(Map, String, File)
     * @deprecated 3.5.0
     */
    @Deprecated
    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull String outputFile) throws Exception {

    }

    /**
     * 将模板转化成为文件
     *
     * @param objectMap    渲染对象 MAP 信息
     * @param templatePath 模板文件
     * @param outputFile   文件生成的目录
     * @throws Exception 异常
     * @since 3.5.0
     */
    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull File outputFile) throws Exception {
        this.writer(objectMap, templatePath, outputFile.getPath());
        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }

    /**
     * 打开输出目录
     */
    public void open() {
        String outDir = getConfigBuilder().getGlobalConfig().getOutputDir();
        if (StringUtils.isBlank(outDir) || !new File(outDir).exists()) {
            System.err.println("未找到输出目录：" + outDir);
        } else if (getConfigBuilder().getGlobalConfig().isOpen()) {
            try {
                RuntimeUtils.openDir(outDir);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 渲染对象 MAP 信息
     *
     * @param config    配置信息
     * @param tableInfo 表信息对象
     * @return ignore
     */
    @NotNull
    public Map<String, Object> getObjectMap(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> objectMap = new HashMap<>();
        StrategyConfig strategyConfig = config.getStrategyConfig();
        Map<String, Object> controllerData = strategyConfig.controller().renderData(config, tableInfo);
        objectMap.putAll(controllerData);
        Map<String, Object> indexVueData = strategyConfig.indexVue().renderData(config, tableInfo);
        objectMap.putAll(indexVueData);
        Map<String, Object> permissionSqlData = strategyConfig.permissionSQL().renderData(config, tableInfo);
        objectMap.putAll(permissionSqlData);
        Map<String, Object> mapperData = strategyConfig.mapper().renderData(config, tableInfo);
        objectMap.putAll(mapperData);
        Map<String, Object> serviceData = strategyConfig.service().renderData(config, tableInfo);
        objectMap.putAll(serviceData);
        Map<String, Object> entityData = strategyConfig.entity().renderData(config, tableInfo);
        objectMap.putAll(entityData);
        Map<String, Object> dtoData = strategyConfig.dto().renderData(config, tableInfo);
        objectMap.putAll(dtoData);
        Map<String, Object> excelsData = strategyConfig.excels().renderData(config, tableInfo);
        objectMap.putAll(excelsData);
        objectMap.put("config", config);
        objectMap.put("package", config.getPackageConfig().getPackageInfo());
        objectMap.put("indexVuePackage", strategyConfig.indexVue().getViewPath());
        GlobalConfig globalConfig = config.getGlobalConfig();
        objectMap.put("author", globalConfig.getAuthor());
        objectMap.put("kotlin", globalConfig.isKotlin());
        objectMap.put("swagger", globalConfig.isSwagger());
        objectMap.put("springdoc", globalConfig.isSpringdoc());
        objectMap.put("date", globalConfig.getCommentDate());
        // 启用 schema 处理逻辑
        String schemaName = "";
        if (strategyConfig.isEnableSchema()) {
            // 存在 schemaName 设置拼接 . 组合表名
            schemaName = config.getDataSourceConfig().getSchemaName();
            if (StringUtils.isNotBlank(schemaName)) {
                schemaName += ".";
                tableInfo.setConvert(true);
            }
        }
        objectMap.put("schemaName", schemaName);
        objectMap.put("table", tableInfo);
        objectMap.put("entity", tableInfo.getEntityName());
        objectMap.put("service", tableInfo.getServiceName());
        Optional<TableField> first = tableInfo.getFields().stream().filter(f -> f.isKeyFlag()).findFirst();
        if (first.isPresent()) {
            // 主键名称
            objectMap.put("entityKeyName", first.get().getPropertyName());
        }
        return objectMap;
    }

    /**
     * 模板真实文件路径
     *
     * @param filePath 文件路径
     * @return ignore
     */
    @NotNull
    public abstract String templateFilePath(@NotNull String filePath);

    /**
     * 检测文件是否存在（3.5.4版本会删除此方法）
     *
     * @return 文件是否存在
     * @deprecated 3.5.0
     */
    @Deprecated
    protected boolean isCreate(String filePath) {
        return isCreate(new File(filePath));
    }

    /**
     * 检查文件是否创建文件（3.5.4版本会删除此方法）
     *
     * @param file 文件
     * @return 是否创建文件
     * @since 3.5.0
     */
    @Deprecated
    protected boolean isCreate(@NotNull File file) {
        // 全局判断【默认】
        return !file.exists();
    }

    /**
     * 检查文件是否创建文件
     *
     * @param file         文件
     * @param fileOverride 是否覆盖已有文件
     * @return 是否创建文件
     * @since 3.5.2
     */
    protected boolean isCreate(@NotNull File file, boolean fileOverride) {
        if (file.exists() && !fileOverride) {
            logger.warn("文件[{}]已存在，且未开启文件覆盖配置，需要开启配置可到策略配置中设置！！！", file.getName());
        }
        return !file.exists() || fileOverride;
    }

    /**
     * 文件后缀
     */
    protected String suffixJavaOrKt() {
        return getConfigBuilder().getGlobalConfig().isKotlin() ? ConstVal.KT_SUFFIX : ConstVal.JAVA_SUFFIX;
    }

    @NotNull
    public ConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    @NotNull
    public AbstractTemplateEngine setConfigBuilder(@NotNull ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }
}
