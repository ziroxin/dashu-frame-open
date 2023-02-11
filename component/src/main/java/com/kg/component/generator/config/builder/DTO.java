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
package com.kg.component.generator.config.builder;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kg.component.generator.IFill;
import com.kg.component.generator.ITemplate;
import com.kg.component.generator.config.ConstVal;
import com.kg.component.generator.config.INameConvert;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.po.TableInfo;
import com.kg.component.generator.config.rules.NamingStrategy;
import com.kg.component.generator.function.ConverterFileName;
import com.kg.component.generator.util.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DTO属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.5.0
 */
public class DTO implements ITemplate {

    private final static Logger LOGGER = LoggerFactory.getLogger(DTO.class);

    private DTO() {
    }

    /**
     * 名称转换
     */
    private INameConvert nameConvert;

    /**
     * 自定义继承的DTO类全称，带包名
     */
    private String superClass;

    /**
     * 子表，表名列表
     * （@ziro add at 20230209 用于生成附件子表List字段，例如：List<ATable2File> aTable2FileList;）
     */
    private List<String> childTableList;

    /**
     * 自定义基础的DTO类，公共字段
     */
    private final Set<String> superDTOColumns = new HashSet<>();

    /**
     * 自定义忽略字段
     * https://github.com/baomidou/generator/issues/46
     */
    private final Set<String> ignoreColumns = new HashSet<>();

    /**
     * 实体是否生成 serialVersionUID
     */
    private boolean serialVersionUID = true;

    /**
     * 【实体】是否生成字段常量（默认 false）<br>
     * -----------------------------------<br>
     * public static final String ID = "test_id";
     */
    private boolean columnConstant;

    /**
     * 【实体】是否为链式模型（默认 false）
     *
     * @since 3.3.2
     */
    private boolean chain;

    /**
     * 【实体】是否为lombok模型（默认 false）<br>
     * <a href="https://projectlombok.org/">document</a>
     */
    private boolean lombok;

    /**
     * Boolean类型字段是否移除is前缀（默认 false）<br>
     * 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
     */
    private boolean booleanColumnRemoveIsPrefix;

    /**
     * 是否生成实体时，生成字段注解（默认 false）
     */
    private boolean tableFieldAnnotationEnable;

    /**
     * 乐观锁字段名称(数据库字段)
     *
     * @since 3.5.0
     */
    private String versionColumnName;

    /**
     * 乐观锁属性名称(实体字段)
     *
     * @since 3.5.0
     */
    private String versionPropertyName;

    /**
     * 逻辑删除字段名称(数据库字段)
     *
     * @since 3.5.0
     */
    private String logicDeleteColumnName;

    /**
     * 逻辑删除属性名称(实体字段)
     *
     * @since 3.5.0
     */
    private String logicDeletePropertyName;

    /**
     * 表填充字段
     */
    private final List<IFill> tableFillList = new ArrayList<>();

    /**
     * 数据库表映射到实体的命名策略，默认下划线转驼峰命名
     */
    private NamingStrategy naming = NamingStrategy.underline_to_camel;

    /**
     * 数据库表字段映射到实体的命名策略
     * <p>未指定按照 naming 执行</p>
     */
    private NamingStrategy columnNaming = null;

    /**
     * 开启 ActiveRecord 模式（默认 false）
     *
     * @since 3.5.0
     */
    private boolean activeRecord;

    /**
     * 指定生成的主键的ID类型
     *
     * @since 3.5.0
     */
    private IdType idType;

    /**
     * 转换输出文件名称
     *
     * @since 3.5.0
     */
    private ConverterFileName converterFileName = (entityName -> entityName + ConstVal.DTO);
    private ConverterFileName dtoconverterFileName = (entityName -> entityName + ConstVal.DTOCONVERT);

    /**
     * 是否覆盖已有文件（默认 false）
     *
     * @since 3.5.2
     */
    private boolean fileOverride;

    /**
     * <p>
     * 父类 Class 反射属性转换为公共字段
     * </p>
     *
     * @param clazz 实体父类 Class
     */
    public void convertSuperDTOColumns(Class<?> clazz) {
        List<Field> fields = TableInfoHelper.getAllFields(clazz);
        this.superDTOColumns.addAll(fields.stream().map(field -> {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
                return tableId.value();
            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
                return tableField.value();
            }
            if (null == columnNaming || columnNaming == NamingStrategy.no_change) {
                return field.getName();
            }
            return StringUtils.camelToUnderline(field.getName());
        }).collect(Collectors.toSet()));
    }

    @NotNull
    public NamingStrategy getColumnNaming() {
        // 未指定以 naming 策略为准
        return Optional.ofNullable(columnNaming).orElse(naming);
    }

    /**
     * 匹配父类字段(忽略大小写)
     *
     * @param fieldName 字段名
     * @return 是否匹配
     * @since 3.5.0
     */
    public boolean matchSuperDTOColumns(String fieldName) {
        // 公共字段判断忽略大小写【 部分数据库大小写不敏感 】
        return superDTOColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

    /**
     * 匹配忽略字段(忽略大小写)
     *
     * @param fieldName 字段名
     * @return 是否匹配
     * @since 3.5.0
     */
    public boolean matchIgnoreColumns(String fieldName) {
        return ignoreColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

    @NotNull
    public INameConvert getNameConvert() {
        return nameConvert;
    }

    @Nullable
    public String getSuperClass() {
        return superClass;
    }

    public List<String> getChildTableList() {
        return childTableList;
    }

    public Set<String> getSuperDTOColumns() {
        return this.superDTOColumns;
    }

    public boolean isSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isColumnConstant() {
        return columnConstant;
    }

    public boolean isChain() {
        return chain;
    }

    public boolean isLombok() {
        return lombok;
    }

    public boolean isBooleanColumnRemoveIsPrefix() {
        return booleanColumnRemoveIsPrefix;
    }

    public boolean isTableFieldAnnotationEnable() {
        return tableFieldAnnotationEnable;
    }

    @Nullable
    public String getVersionColumnName() {
        return versionColumnName;
    }

    @Nullable
    public String getVersionPropertyName() {
        return versionPropertyName;
    }

    @Nullable
    public String getLogicDeleteColumnName() {
        return logicDeleteColumnName;
    }

    @Nullable
    public String getLogicDeletePropertyName() {
        return logicDeletePropertyName;
    }

    @NotNull
    public List<IFill> getTableFillList() {
        return tableFillList;
    }

    @NotNull
    public NamingStrategy getNaming() {
        return naming;
    }

    public boolean isActiveRecord() {
        return activeRecord;
    }

    @Nullable
    public IdType getIdType() {
        return idType;
    }

    @NotNull
    public ConverterFileName getConverterFileName() {
        return converterFileName;
    }

    @NotNull
    public ConverterFileName getConverterDtoconvertFileName() {
        return dtoconverterFileName;
    }

    public boolean isFileOverride() {
        return fileOverride;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("activeRecord", this.activeRecord);
        data.put("dtoSerialVersionUID", this.serialVersionUID);
        data.put("dtoColumnConstant", this.columnConstant);
        data.put("chainModel", this.chain);
        data.put("dtoLombokModel", this.lombok);
        data.put("superDTOClassPackage", StringUtils.isBlank(superClass) ? null : superClass);
        data.put("superDTOClass", ClassUtils.getSimpleName(this.superClass));
        if (this.childTableList != null && this.childTableList.size() > 0) {
            data.put("childTableList", this.childTableList);
            data.put("packageBaseParent", config.getPackageConfig().getBaseParent());
        }
        data.put("dtoName", tableInfo.getDTOName());
        data.put("dtoconvertName", tableInfo.getDtoconvertName());
        return data;
    }

    public static class Builder extends BaseBuilder {

        private final DTO dto = new DTO();

        public Builder(StrategyConfig strategyConfig) {
            super(strategyConfig);
            this.dto.nameConvert = new INameConvert.DefaultNameConvert(strategyConfig);
        }

        /**
         * 名称转换实现
         *
         * @param nameConvert 名称转换实现
         * @return this
         */
        public Builder nameConvert(INameConvert nameConvert) {
            this.dto.nameConvert = nameConvert;
            return this;
        }

        /**
         * 自定义继承的DTO类全称
         *
         * @param clazz 类
         * @return this
         */
        public Builder superClass(@NotNull Class<?> clazz) {
            return superClass(clazz.getName());
        }

        /**
         * 自定义继承的DTO类全称，带包名
         *
         * @param superDTOClass 类全称
         * @return this
         */
        public Builder superClass(String superDTOClass) {
            this.dto.superClass = superDTOClass;
            return this;
        }

        /**
         * 自定义子表列表
         *
         * @param childTableList 子表列表
         * @return this
         */
        public Builder childTableList(List<String> childTableList) {
            this.dto.childTableList = childTableList;
            return this;
        }


        /**
         * 禁用生成serialVersionUID
         *
         * @return this
         * @since 3.5.0
         */
        public Builder disableSerialVersionUID() {
            this.dto.serialVersionUID = false;
            return this;
        }

        /**
         * 开启生成字段常量
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableColumnConstant() {
            this.dto.columnConstant = true;
            return this;
        }

        /**
         * 开启链式模型
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableChainModel() {
            this.dto.chain = true;
            return this;
        }

        /**
         * 开启lombok模型
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableLombok() {
            this.dto.lombok = true;
            return this;
        }

        /**
         * 开启Boolean类型字段移除is前缀
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableRemoveIsPrefix() {
            this.dto.booleanColumnRemoveIsPrefix = true;
            return this;
        }

        /**
         * 开启生成实体时生成字段注解
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableTableFieldAnnotation() {
            this.dto.tableFieldAnnotationEnable = true;
            return this;
        }

        /**
         * 开启 ActiveRecord 模式
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableActiveRecord() {
            this.dto.activeRecord = true;
            return this;
        }

        /**
         * 设置乐观锁数据库表字段名称
         *
         * @param versionColumnName 乐观锁数据库字段名称
         * @return this
         */
        public Builder versionColumnName(String versionColumnName) {
            this.dto.versionColumnName = versionColumnName;
            return this;
        }

        /**
         * 设置乐观锁实体属性字段名称
         *
         * @param versionPropertyName 乐观锁实体属性字段名称
         * @return this
         */
        public Builder versionPropertyName(String versionPropertyName) {
            this.dto.versionPropertyName = versionPropertyName;
            return this;
        }

        /**
         * 逻辑删除数据库字段名称
         *
         * @param logicDeleteColumnName 逻辑删除字段名称
         * @return this
         */
        public Builder logicDeleteColumnName(String logicDeleteColumnName) {
            this.dto.logicDeleteColumnName = logicDeleteColumnName;
            return this;
        }

        /**
         * 逻辑删除实体属性名称
         *
         * @param logicDeletePropertyName 逻辑删除实体属性名称
         * @return this
         */
        public Builder logicDeletePropertyName(String logicDeletePropertyName) {
            this.dto.logicDeletePropertyName = logicDeletePropertyName;
            return this;
        }

        /**
         * 数据库表映射到实体的命名策略
         *
         * @param namingStrategy 数据库表映射到实体的命名策略
         * @return this
         */
        public Builder naming(NamingStrategy namingStrategy) {
            this.dto.naming = namingStrategy;
            return this;
        }

        /**
         * 数据库表字段映射到实体的命名策略
         *
         * @param namingStrategy 数据库表字段映射到实体的命名策略
         * @return this
         */
        public Builder columnNaming(NamingStrategy namingStrategy) {
            this.dto.columnNaming = namingStrategy;
            return this;
        }

        /**
         * 添加父类公共字段
         *
         * @param superDTOColumns 父类字段(数据库字段列名)
         * @return this
         * @since 3.5.0
         */
        public Builder addSuperDTOColumns(@NotNull String... superDTOColumns) {
            return addSuperDTOColumns(Arrays.asList(superDTOColumns));
        }

        public Builder addSuperDTOColumns(@NotNull List<String> superDTOColumnList) {
            this.dto.superDTOColumns.addAll(superDTOColumnList);
            return this;
        }

        /**
         * 添加忽略字段
         *
         * @param ignoreColumns 需要忽略的字段(数据库字段列名)
         * @return this
         * @since 3.5.0
         */
        public Builder addIgnoreColumns(@NotNull String... ignoreColumns) {
            return addIgnoreColumns(Arrays.asList(ignoreColumns));
        }

        public Builder addIgnoreColumns(@NotNull List<String> ignoreColumnList) {
            this.dto.ignoreColumns.addAll(ignoreColumnList);
            return this;
        }

        /**
         * 添加表字段填充
         *
         * @param tableFills 填充字段
         * @return this
         * @since 3.5.0
         */
        public Builder addTableFills(@NotNull IFill... tableFills) {
            return addTableFills(Arrays.asList(tableFills));
        }

        /**
         * 添加表字段填充
         *
         * @param tableFillList 填充字段集合
         * @return this
         * @since 3.5.0
         */
        public Builder addTableFills(@NotNull List<IFill> tableFillList) {
            this.dto.tableFillList.addAll(tableFillList);
            return this;
        }

        /**
         * 指定生成的主键的ID类型
         *
         * @param idType ID类型
         * @return this
         * @since 3.5.0
         */
        public Builder idType(IdType idType) {
            this.dto.idType = idType;
            return this;
        }

        /**
         * 转换输出文件名称
         *
         * @param converter 　转换处理
         * @return this
         * @since 3.5.0
         */
        public Builder convertFileName(@NotNull ConverterFileName converter) {
            this.dto.converterFileName = converter;
            return this;
        }

        /**
         * 格式化文件名称
         *
         * @param format 　格式
         * @return this
         * @since 3.5.0
         */
        public Builder formatFileName(String format) {
            return convertFileName((dtoName) -> String.format(format, dtoName));
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public Builder enableFileOverride() {
            this.dto.fileOverride = true;
            return this;
        }

        public DTO get() {
            String superClass = this.dto.superClass;
            if (StringUtils.isNotBlank(superClass)) {
                tryLoadClass(superClass).ifPresent(this.dto::convertSuperDTOColumns);
            } else {
                if (!this.dto.superDTOColumns.isEmpty()) {
                    LOGGER.warn("Forgot to set dto supper class ?");
                }
            }
            return this.dto;
        }

        private Optional<Class<?>> tryLoadClass(String className) {
            try {
                return Optional.of(ClassUtils.toClassConfident(className));
            } catch (Exception e) {
                //当父类实体存在类加载器的时候,识别父类实体字段，不存在的情况就只有通过指定superDTOColumns属性了。
            }
            return Optional.empty();
        }
    }
}
