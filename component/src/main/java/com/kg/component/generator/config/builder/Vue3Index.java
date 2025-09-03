package com.kg.component.generator.config.builder;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kg.component.generator.ITemplate;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * index.vue生成属性配置
 *
 * @author ziro
 * @date 2025-08-09 15:30:00
 */
public class Vue3Index implements ITemplate {

    private Vue3Index() {
    }

    // 生成文件地址
    private String viewPath;
    // 是否覆盖代码
    private boolean fileOverride;
    // 查询字段列表
    private List<String> searchFields;
    // 列表字段列表
    private List<String> listFields;
    // 附件字段名
    private String attachmentField2;

    public String getViewPath() {
        return this.viewPath;
    }

    public boolean isFileOverride() {
        return fileOverride;
    }

    public List<String> getSearchFields() {
        return searchFields;
    }

    public List<String> getListFields() {
        return listFields;
    }

    public String getAttachmentField2() {
        return attachmentField2;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>(10);
        String packageStr = config.getPackageConfig().getModuleName();
        if (StringUtils.isNotBlank(packageStr)) {
            data.put("controllerMapping", packageStr.replaceAll("\\.", "/") + "/" +
                    tableInfo.getEntityPath());
            data.put("buttonNamePre", packageStr.replaceAll("\\.", "-") + "-" +
                    tableInfo.getEntityPath() + "-");
        } else {
            data.put("controllerMapping", tableInfo.getEntityPath());
            data.put("buttonNamePre", tableInfo.getEntityPath() + "-");
        }
        data.put("searchFields", getSearchFields());
        data.put("listFields", getListFields());
        data.put("hasDeleteLog", config.getStrategyConfig().service().hasDeleteLog());

        data.put("attachmentField2", getAttachmentField2());
        return data;
    }

    public static class Builder extends BaseBuilder {

        private final Vue3Index vue3Index = new Vue3Index();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        public Builder viewPath(String viewPath) {
            this.vue3Index.viewPath = viewPath;
            return this;
        }

        public Builder searchFields(List<String> searchFields) {
            this.vue3Index.searchFields = searchFields;
            return this;
        }

        public Builder listFields(List<String> listFields) {
            this.vue3Index.listFields = listFields;
            return this;
        }

        public Builder attachmentField2(String attachmentField2) {
            this.vue3Index.attachmentField2 = attachmentField2;
            return this;
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public Builder enableFileOverride() {
            this.vue3Index.fileOverride = true;
            return this;
        }

        @NotNull
        public Vue3Index get() {
            return this.vue3Index;
        }
    }

}
