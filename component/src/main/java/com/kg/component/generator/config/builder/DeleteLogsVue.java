package com.kg.component.generator.config.builder;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kg.component.generator.ITemplate;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * deleteLogs.vue生成属性配置
 *
 * @author ziro
 * @date 2025/5/30 9:48
 */
public class DeleteLogsVue implements ITemplate {

    private DeleteLogsVue() {
    }

    // 生成文件地址
    private String viewPath;
    // 是否覆盖代码
    private boolean fileOverride;
    // 附件字段名
    private String attachmentField3;

    public String getViewPath() {
        return this.viewPath;
    }

    public boolean isFileOverride() {
        return fileOverride;
    }

    public String getAttachmentField3() {
        return attachmentField3;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>(2);
        String packageStr = config.getPackageConfig().getModuleName();
        if (StringUtils.isNotBlank(packageStr)) {
            data.put("controllerMapping", packageStr.replaceAll("\\.", "/") + "/" +
                    tableInfo.getEntityPath());
        } else {
            data.put("controllerMapping", tableInfo.getEntityPath());
        }

        data.put("attachmentField3", getAttachmentField3());
        return data;
    }

    public static class Builder extends BaseBuilder {
        private final DeleteLogsVue deleteLogsVue = new DeleteLogsVue();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        public DeleteLogsVue.Builder viewPath(String viewPath) {
            this.deleteLogsVue.viewPath = viewPath;
            return this;
        }

        public DeleteLogsVue.Builder attachmentField3(String attachmentField3) {
            this.deleteLogsVue.attachmentField3 = attachmentField3;
            return this;
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public DeleteLogsVue.Builder enableFileOverride() {
            this.deleteLogsVue.fileOverride = true;
            return this;
        }

        @NotNull
        public DeleteLogsVue get() {
            return this.deleteLogsVue;
        }
    }
}
