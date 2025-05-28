package com.kg.core.formGenerator.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.kg.component.generator.FastAutoGenerator;
import com.kg.component.generator.config.OutputFile;
import com.kg.component.generator.engine.FreemarkerTemplateEngine;
import com.kg.component.generator.fill.Column;
import com.kg.core.base.dto.BaseDTO;
import com.kg.core.base.model.BaseEntity;
import com.kg.core.formGenerator.dto.TableDTO;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
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

    // 代码生成器开始生成
    public void start(String basePath, String module, String basePackage, String author, String vueFolder,
                      LinkedList<String> tableNames, LinkedList<IdType> idTypes,
                      LinkedList<String> packages, LinkedList<String> viewPaths,
                      TableDTO tableDTO, Map<String, Object> childTableMap) {
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
            // 权限脚本，输出到sql
            pathInfo.put(OutputFile.permissionSql, basePath + "/sql");
            // 配置生成器
            int finalIndex = i;
            FastAutoGenerator.create(dbUrl, dbUserName, dbPassword)
                    .globalConfig(builder -> {
                        builder.author(author) // 设置作者
                                .enableSwagger() // 开启swagger模式
//                                .disableOpenDir() // 禁止打开输出目录
                                .outputDir(javaPath); // 指定输出目录
                    })
                    .packageConfig(builder -> {
                        builder.parent(basePackage) // 设置父包名
                                .moduleName(packages.get(finalIndex)) // 设置父包模块名
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
                            builder.addInclude(tableNames.get(finalIndex));
                            // ===============indexVue配置
                            if (viewPaths != null && viewPaths.size() > finalIndex && StringUtils.hasText(viewPaths.get(finalIndex))) {
                                if (tableDTO == null) {
                                    builder.indexVueBuilder()// ===============indexVue配置
                                            .enableFileOverride()
                                            .viewPath(viewPaths.get(finalIndex));// 前端文件路径
                                } else {
                                    builder.indexVueBuilder()
                                            .templateHtml(URLDecoder.decode(tableDTO.getTemplate(), "UTF-8"))
                                            .jsData(URLDecoder.decode(tableDTO.getJsData(), "UTF-8"))
                                            .jsCreated(URLDecoder.decode(tableDTO.getJsCreated(), "UTF-8"))
                                            .jsMethods(URLDecoder.decode(tableDTO.getJsMethods(), "UTF-8"))
                                            .templateCss(URLDecoder.decode(tableDTO.getCss(), "UTF-8"))
                                            .enableFileOverride()
                                            .viewPath(viewPaths.get(finalIndex));// 前端文件路径
                                }
                            }
                            // ====================DTO配置（配置附件子表信息）
                            LinkedList<String> childTableList = childTableMap == null ? null : (LinkedList) childTableMap.get(tableNames.get(finalIndex));
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
                            // ==========更多配置
                            builder.permissionSQLBuilder()// ==========permissionSQL配置
                                    .enableFileOverride()
                                    .controllerBuilder()// =============controller配置
                                    .enableFileOverride()
                                    .enableRestStyle()// 开启RestController
                                    .serviceBuilder()// ================service配置
                                    .enableFileOverride()
                                    .mapperBuilder()// =================mapper配置
                                    .enableFileOverride()
                                    .excelsBuilder()// =================excel配置
                                    .enableFileOverride()
                                    .entityBuilder()// =================entity配置
                                    .enableFileOverride()
                                    .superClass(BaseEntity.class)
                                    .idType(idTypes.get(finalIndex))// 生成id类型
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
    }
}
