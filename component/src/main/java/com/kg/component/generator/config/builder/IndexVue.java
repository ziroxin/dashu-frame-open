package com.kg.component.generator.config.builder;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kg.component.generator.ITemplate;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * index.vue生成属性配置
 *
 * @author ziro
 * @date 2022-08-19 20:37:06
 */
public class IndexVue implements ITemplate {
    private final static Logger LOGGER = LoggerFactory.getLogger(IndexVue.class);

    private IndexVue() {
    }

    private String viewPath;

    private boolean fileOverride;

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getViewPath() {
        return this.viewPath;
    }

    public boolean isFileOverride() {
        return fileOverride;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>(2);
        String mapping = (StringUtils.isNotBlank(config.getPackageConfig().getModuleName()) ? config.getPackageConfig().getModuleName() + "/" : "")
                + tableInfo.getEntityPath();
        data.put("controllerMapping", mapping);
        String buttonNamePre = (StringUtils.isNotBlank(config.getPackageConfig().getModuleName()) ? config.getPackageConfig().getModuleName() + "-" : "")
                + tableInfo.getEntityPath() + "-";
        data.put("buttonNamePre", buttonNamePre);
        return data;
    }

    public static class Builder extends BaseBuilder {

        private final IndexVue indexVue = new IndexVue();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        public Builder viewPath(String viewPath) {
            this.indexVue.viewPath = viewPath;
            return this;
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public Builder enableFileOverride() {
            this.indexVue.fileOverride = true;
            return this;
        }

        @NotNull
        public IndexVue get() {
            return this.indexVue;
        }
    }

}
