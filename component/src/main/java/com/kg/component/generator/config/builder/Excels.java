package com.kg.component.generator.config.builder;

import com.kg.component.generator.ITemplate;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel相关文件 - 生成属性配置
 *
 * @author ziro
 * @date 2022-12-29 10:39:43
 */
public class Excels implements ITemplate {

    private Excels() {
    }

    // 是否覆盖代码
    private boolean fileOverride;
    // 导入模板字段列表
    private List<String> importFields;
    // 导出模板字段列表
    private List<String> exportFields;

    public boolean isFileOverride() {
        return fileOverride;
    }

    public List<String> getImportFields() {
        return importFields;
    }

    public List<String> getExportFields() {
        return exportFields;
    }


    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("importFields", getImportFields());
        data.put("exportFields", getExportFields());
        return data;
    }


    public static class Builder extends BaseBuilder {

        private final Excels excels = new Excels();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        public Builder setImportFields(List<String> importFields) {
            this.excels.importFields = importFields;
            return this;
        }

        public Builder setExportFields(List<String> exportFields) {
            this.excels.exportFields = exportFields;
            return this;
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public Excels.Builder enableFileOverride() {
            this.excels.fileOverride = true;
            return this;
        }

        @NotNull
        public Excels get() {
            return this.excels;
        }
    }
}
