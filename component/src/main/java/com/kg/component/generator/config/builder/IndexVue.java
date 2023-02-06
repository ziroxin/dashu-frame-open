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

    // 生成文件地址
    private String viewPath;
    // 是否覆盖代码
    private boolean fileOverride;
    // 生成代码-模板
    private String templateHtml;
    // 生成代码-脚本data
    private String jsData;
    // 生成代码-脚本created
    private String jsCreated;
    // 生成代码-脚本methods
    private String jsMethods;
    // 生成代码-样式
    private String templateCss;

    public String getViewPath() {
        return this.viewPath;
    }

    public boolean isFileOverride() {
        return fileOverride;
    }

    public String getTemplateHtml() {
        return templateHtml;
    }

    public String getJsData() {
        return jsData;
    }

    public String getJsCreated() {
        return jsCreated;
    }

    public String getJsMethods() {
        return jsMethods;
    }

    public String getTemplateCss() {
        return templateCss;
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
        data.put("templateHtml", getTemplateHtml());
        data.put("jsData", getJsData());
        data.put("jsCreated", getJsCreated());
        data.put("jsMethods", getJsMethods());
        data.put("templateCss", getTemplateCss());
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

        public Builder templateHtml(String templateHtml) {
            this.indexVue.templateHtml = templateHtml;
            return this;
        }

        public Builder jsData(String jsData) {
            this.indexVue.jsData = jsData;
            return this;
        }

        public Builder jsCreated(String jsCreated) {
            this.indexVue.jsCreated = jsCreated;
            return this;
        }

        public Builder jsMethods(String jsMethods) {
            this.indexVue.jsMethods = jsMethods;
            return this;
        }

        public Builder templateCss(String templateCss) {
            this.indexVue.templateCss = templateCss;
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
