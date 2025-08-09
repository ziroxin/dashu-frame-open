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
 * @date 2025-08-09 15:30:00
 */
public class Vue3DeleteLogs implements ITemplate {

    private Vue3DeleteLogs() {
    }

    // 生成文件地址
    private String viewPath;
    // 是否覆盖代码
    private boolean fileOverride;

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
        String packageStr = config.getPackageConfig().getModuleName();
        if (StringUtils.isNotBlank(packageStr)) {
            data.put("controllerMapping", packageStr.replaceAll("\\.", "/") + "/" +
                    tableInfo.getEntityPath());
        } else {
            data.put("controllerMapping", tableInfo.getEntityPath());
        }
        return data;
    }

    public static class Builder extends BaseBuilder {
        private final Vue3DeleteLogs vue3DeleteLogs = new Vue3DeleteLogs();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        public Vue3DeleteLogs.Builder viewPath(String viewPath) {
            this.vue3DeleteLogs.viewPath = viewPath;
            return this;
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public Vue3DeleteLogs.Builder enableFileOverride() {
            this.vue3DeleteLogs.fileOverride = true;
            return this;
        }

        @NotNull
        public Vue3DeleteLogs get() {
            return this.vue3DeleteLogs;
        }
    }
}
