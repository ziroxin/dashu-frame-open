package com.kg.core.formGenerator.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.kg.component.generator.FastAutoGenerator;
import com.kg.component.generator.config.OutputFile;
import com.kg.component.generator.engine.FreemarkerTemplateEngine;
import com.kg.component.generator.fill.Column;
import com.kg.component.utils.TimeUtils;
import com.kg.core.base.dto.BaseDTO;
import com.kg.core.base.model.BaseEntity;
import com.kg.core.formGenerator.dto.TableDTO;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * MybatisPlus代码生成器工具类
 *
 * @author ziro
 * @date 2025/5/28 15:59
 */
@Component
@Getter
public class GeneratorCodeUtils {
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUserName;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 代码生成器 - 入口方法
     *
     * @param basePath      生成文件根路径
     * @param module        后台代码文件夹，如：module
     * @param basePackage   包路径，如：com.kg.module
     * @param author        作者
     * @param vueFolder     前台代码文件夹，如：web-vue2
     * @param tableNames    待生成表名列表
     * @param idTypes       主键类型列表
     * @param packages      包名列表
     * @param viewPaths     前端文件夹名列表
     * @param tableDTO      前端页面扩展代码（可视化代码生成器用）
     * @param childTableMap 附件子表信息（可视化代码生成器用）
     * @param hasDeleteLogs 是否生成删除日志表（默认不生成）
     *                      要求：1日志表名[表名_logs]； 2日志表字段包含全部主表字段，并增加[主键logs_id和删除时间delete_time]字段
     */
    public void start(String basePath, String module, String basePackage, String author, String vueFolder,
                      LinkedList<String> tableNames, LinkedList<IdType> idTypes,
                      LinkedList<String> packages, LinkedList<String> viewPaths,
                      TableDTO tableDTO, Map<String, Object> childTableMap, boolean hasDeleteLogs) {
        for (int i = 0; i < tableNames.size(); i++) {
            // ===========================================执行生成=======================
            // 配置文件路径
            Map<OutputFile, String> pathInfo = new HashMap<>();
            // java代码输出路径
            String javaPath = basePath + "/" + module + "/src/main/java";
            // xml代码输出路径
            pathInfo.put(OutputFile.xml, basePath + "/" + module + "/src/main/resources/mapper");
            // 前台代码，输出到vue
            pathInfo.put(OutputFile.indexVue, basePath + "/" + vueFolder + "/src/views");
            pathInfo.put(OutputFile.deleteLogsVue, basePath + "/" + vueFolder + "/src/views");
            // 权限脚本，输出到sql
            pathInfo.put(OutputFile.permissionSql, basePath + "/sql");
            // 配置生成器
            int finalIndex = i;
            // 表名
            String tableName = tableNames.get(finalIndex);
            // 前端文件路径
            String indexViewPath = viewPaths != null && viewPaths.size() > finalIndex && StringUtils.hasText(viewPaths.get(finalIndex)) ? viewPaths.get(finalIndex) : null;
            // 主键类型
            IdType idType = idTypes.get(finalIndex);
            // 包名
            String packageStr = packages.get(finalIndex);
            // ========== 1. 生成主表代码 ==========
            generatorCode(author, javaPath, basePackage, pathInfo, tableName, indexViewPath, null,
                    idType, packageStr, tableDTO, childTableMap, hasDeleteLogs);
            // 是否生成删除日志，默认不生成
            if (hasDeleteLogs) {
                // ========== 2. 生成删除日志表代码 ==========
                String tableName2 = tableName + "_logs";
                // 2.1 生成删除日志表
                createDeleteLogsTable(tableName, tableName2);
                // 2.2 生成删除日志功能代码
                String deleteLogsViewPath = StringUtils.hasText(indexViewPath) ? indexViewPath + "/deleteLogs" : null;
                generatorCode(author, javaPath, basePackage, pathInfo, tableName2, null, deleteLogsViewPath,
                        IdType.ASSIGN_UUID, packageStr, null, null, false);
            }
        }
    }

    /**
     * 生成删除日志表
     *
     * @param author             作者
     * @param javaPath           生成java文件根路径
     * @param basePackage        包路径（父包名），如：com.kg.module
     * @param pathInfo           输出路径信息
     * @param tableName          表名
     * @param indexViewPath      前端文件路径
     * @param deleteLogsViewPath 删除日志前端文件路径（只在生成删除日志表时有效）
     * @param idType             主键类型
     * @param packageStr         生成代码包名
     * @param tableDTO           前端页面扩展代码（可视化代码生成器用）
     * @param childTableMap      附件子表信息（可视化代码生成器用）
     * @param isDeleteLogs       是否生成删除日志表（主表代码生成时，是否输出删除日志相关的代码）
     */
    private void generatorCode(String author, String javaPath, String basePackage,
                               Map<OutputFile, String> pathInfo,
                               String tableName, String indexViewPath, String deleteLogsViewPath,
                               IdType idType, String packageStr,
                               TableDTO tableDTO, Map<String, Object> childTableMap,
                               boolean isDeleteLogs) {
        FastAutoGenerator.create(dbUrl, dbUserName, dbPassword)
                .globalConfig(builder -> {
                    builder.author(author) // 设置作者
                            .enableSwagger() // 开启swagger模式
//                                .disableOpenDir() // 禁止打开输出目录
                            .outputDir(javaPath); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(basePackage) // 设置父包名
                            .moduleName(packageStr) // 设置父包模块名
                            // 设置各包路径
                            .entity("entity")
                            .dto("dto")
                            .dtoconvert("dto.convert")
                            .excelConstant("excels")
                            .excelOut("excels")
                            .excelImport("excels")
                            .service("service")
                            .serviceImpl("service")
                            .mapper("mapper")
                            .xml("mapper")
                            .controller("controller")
                            .pathInfo(pathInfo);
                })
                .strategyConfig(builder -> {
                    try {
                        // 设置需要生成的表名
                        builder.addInclude(tableName);
                        // ===============indexVue配置
                        if (StringUtils.hasText(indexViewPath)) {
                            if (tableDTO == null) {
                                builder.indexVueBuilder()// ===============indexVue配置
                                        .enableFileOverride()
                                        .viewPath(indexViewPath);// 前端文件路径
                            } else {
                                builder.indexVueBuilder()
                                        .templateHtml(URLDecoder.decode(tableDTO.getTemplate(), "UTF-8"))
                                        .jsData(URLDecoder.decode(tableDTO.getJsData(), "UTF-8"))
                                        .jsCreated(URLDecoder.decode(tableDTO.getJsCreated(), "UTF-8"))
                                        .jsMethods(URLDecoder.decode(tableDTO.getJsMethods(), "UTF-8"))
                                        .templateCss(URLDecoder.decode(tableDTO.getCss(), "UTF-8"))
                                        .searchFields(tableDTO.getSearchFields())
                                        .listFields(tableDTO.getListFields())
                                        .enableFileOverride()
                                        .viewPath(indexViewPath);// 前端文件路径
                            }
                        }
                        // ===============deleteLogsVue配置
                        if (StringUtils.hasText(deleteLogsViewPath)) {
                            builder.deleteLogsVueBuilder()// ===============deleteLogsVue配置
                                    .enableFileOverride()
                                    .viewPath(deleteLogsViewPath);// 前端文件路径
                        }
                        // ====================DTO配置（配置附件子表信息）
                        LinkedList<String> childTableList = childTableMap == null ? null : (LinkedList) childTableMap.get(tableName);
                        if (childTableList != null && childTableList.size() > 0) {
                            builder.dtoBuilder()// ====================DTO配置（配置附件子表信息）
                                    .enableFileOverride()
                                    .superClass(BaseDTO.class)
                                    .childTableList(childTableList)
                                    .enableLombok();
                        } else {
                            builder.dtoBuilder()// ====================DTO配置
                                    .enableFileOverride()
                                    .superClass(BaseDTO.class)
                                    .enableLombok();
                        }
                        // ====================excel配置
                        if (tableDTO == null) {
                            builder.excelsBuilder()// =================excel配置
                                    .enableFileOverride();
                        } else {
                            builder.excelsBuilder()// =================excel配置
                                    .setImportFields(tableDTO.getImportFields())
                                    .setExportFields(tableDTO.getExportFields())
                                    .enableFileOverride();
                        }
                        // ==========更多配置
                        builder.permissionSQLBuilder()// ==========permissionSQL配置
                                .enableFileOverride()
                                .controllerBuilder()// =============controller配置
                                .enableFileOverride()
                                .enableRestStyle()// 开启RestController
                                .serviceBuilder()// ================service配置
                                .enableFileOverride()
                                .hasDeleteLogs(isDeleteLogs)
                                .mapperBuilder()// =================mapper配置
                                .enableFileOverride()
                                .entityBuilder()// =================entity配置
                                .enableFileOverride()
                                .superClass(BaseEntity.class)
                                .idType(idType)// 生成id类型
                                .enableTableFieldAnnotation()// 生成TableField
                                .addTableFills(new Column("create_time", FieldFill.INSERT))// 创建时间
                                .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))// 修改时间
                                .enableLombok();// 设置生成lombok格式
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

    /**
     * 生成[删除日志表]
     *
     * @param tableName  主表名
     * @param tableName2 删除日志表名
     */
    private void createDeleteLogsTable(String tableName, String tableName2) {
        // 1 检查是否有用重名的表，若存在，则备份该表
        if (hasTables(tableName2)) {
            // 获取原表的创建语句
            String sql = jdbcTemplate.queryForMap("SHOW CREATE TABLE " + tableName2).get("Create Table").toString();
            // 替换新表名
            String newTblName = tableName2 + "_bak_" + TimeUtils.now().toFormat("yyyyMMddHHmmss");
            sql = sql.replace("CREATE TABLE `" + tableName2, "CREATE TABLE `" + newTblName);
            // 备份原表
            jdbcTemplate.execute(sql);
        }
        // 2 删除表
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName2 + ";");
        // 3 复制主表结构
        String sql1 = "CREATE TABLE " + tableName2 + " LIKE " + tableName + ";";
        jdbcTemplate.execute(sql1);
        // 4 增加[主键logs_id和删除时间delete_time]字段
        String sql2 = "ALTER TABLE " + tableName2 +
                " DROP PRIMARY KEY, " + // 删除原来的主键
                " ADD COLUMN `logs_id` varchar(36) NOT NULL COMMENT '日志主键' FIRST, " + // 将logs_id放在表最前面
                " ADD COLUMN `delete_time` datetime NOT NULL COMMENT '删除时间', " + // 添加delete_time字段到最后面
                " ADD PRIMARY KEY (`logs_id`);"; // 将logs_id设置成主键
        jdbcTemplate.execute(sql2);
        // 5 修改表注释（在原表注释后面添加‘删除日志表’）
        String comment = jdbcTemplate.queryForObject("SELECT TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?",
                String.class, tableName2);
        String sql3 = "ALTER TABLE " + tableName2 + " COMMENT = '" + comment + "-删除日志表';";
        jdbcTemplate.execute(sql3);
    }

    /**
     * 检查是否有用重名的表
     *
     * @param tableName 表名
     * @return 是否有用重名的表
     */
    public boolean hasTables(String tableName) {
        String sql = "SHOW TABLES LIKE ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, tableName);
        return list != null && list.size() > 0;
    }
}
