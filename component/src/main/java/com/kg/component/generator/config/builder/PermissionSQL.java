package com.kg.component.generator.config.builder;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kg.component.generator.ITemplate;
import com.kg.component.generator.config.StrategyConfig;
import com.kg.component.generator.config.po.TableInfo;
import com.kg.component.utils.GuidUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * permission.sql生成属性配置
 *
 * @author ziro
 * @date 2022-08-19 20:37:06
 */
public class PermissionSQL implements ITemplate {
    private final static Logger LOGGER = LoggerFactory.getLogger(PermissionSQL.class);

    private PermissionSQL() {
    }

    private boolean fileOverride;

    public boolean isFileOverride() {
        return fileOverride;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();
        String mapping = (StringUtils.isNotBlank(config.getPackageConfig().getModuleName()) ? "/" + config.getPackageConfig().getModuleName() : "")
                + "/" + tableInfo.getEntityPath();
        data.put("controllerMapping", mapping);
        String authorize = (StringUtils.isNotBlank(config.getPackageConfig().getModuleName()) ? config.getPackageConfig().getModuleName() + ":" : "")
                + tableInfo.getEntityPath() + ":";
        data.put("controllerAuthorizePre", authorize);
        String buttonNamePre = (StringUtils.isNotBlank(config.getPackageConfig().getModuleName()) ? config.getPackageConfig().getModuleName() + "-" : "")
                + tableInfo.getEntityPath();
        data.put("permissionName", buttonNamePre);
        String permissionRouter = config.getStrategyConfig().indexVue().getViewPath();
        // 路由
        data.put("permissionRouter", permissionRouter);
        // 组件
        data.put("permissionCompontent", permissionRouter + "/index");
        // 待生成数据id组合
        data.put("menuId", GuidUtils.getUuid());
        data.put("addBtnId", GuidUtils.getUuid());
        data.put("updateBtnId", GuidUtils.getUuid());
        data.put("deleteBtnId", GuidUtils.getUuid());
        data.put("exportExcelBtnId", GuidUtils.getUuid());
        data.put("addApiId", GuidUtils.getUuid());
        data.put("updateApiId", GuidUtils.getUuid());
        data.put("deleteApiId", GuidUtils.getUuid());
        data.put("exportExcelApiId", GuidUtils.getUuid());
        data.put("listApiId", GuidUtils.getUuid());
        data.put("getByIdApiId", GuidUtils.getUuid());
        return data;
    }

    public static class Builder extends BaseBuilder {

        private final PermissionSQL permissionSQL = new PermissionSQL();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public PermissionSQL.Builder enableFileOverride() {
            this.permissionSQL.fileOverride = true;
            return this;
        }

        @NotNull
        public PermissionSQL get() {
            return this.permissionSQL;
        }
    }

}
