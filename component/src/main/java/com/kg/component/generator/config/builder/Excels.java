package com.kg.component.generator.config.builder;

import com.kg.component.generator.ITemplate;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Excel相关文件 - 生成属性配置
 *
 * @author ziro
 * @date 2022-12-29 10:39:43
 */
public class Excels implements ITemplate {
    private final static Logger LOGGER = LoggerFactory.getLogger(Excels.class);

    private Excels() {
    }

    private boolean fileOverride;

    public boolean isFileOverride() {
        return fileOverride;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();
//        data.put("dtoName", tableInfo.getDTOName());
//        data.put("dtoconvertName", tableInfo.getDtoconvertName());
        return data;
    }


    public static class Builder extends BaseBuilder {

        private final Excels excels = new Excels();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
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
