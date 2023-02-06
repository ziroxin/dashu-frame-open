package com.kg.core.formGenerator.controller;

import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.kg.component.file.FilePathConfig;
import com.kg.component.generator.FastAutoGenerator;
import com.kg.component.generator.config.OutputFile;
import com.kg.component.generator.engine.FreemarkerTemplateEngine;
import com.kg.component.generator.fill.Column;
import com.kg.component.generator.util.RuntimeUtils;
import com.kg.core.base.dto.BaseDTO;
import com.kg.core.base.model.BaseEntity;
import com.kg.core.exception.BaseException;
import com.kg.core.formGenerator.dto.TableDTO;
import com.kg.core.formGenerator.dto.TableFieldDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FormGenerator 代码生成器
 *
 * @author ziro
 * @date 2023-02-03 15:42:40
 */
@RestController
@Validated
public class FormGeneratorController {
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUserName;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    @PostMapping("generator/code/byform")
    public String generate(@RequestBody TableDTO tableDTO) throws BaseException {
        // 1.生成表
        createTable(tableDTO);
        // 2.生成代码
        return generateCode(tableDTO);
    }

    // 生成代码
    private String generateCode(TableDTO tableDTO) {
        // 输出临时
        String basePath = FilePathConfig.SAVE_PATH + "/generate/code/temp/" + tableDTO.getTableName();
        basePath = basePath.replaceAll("//", "/");
        // pom后台模块名（要和后台文件夹保持一致）
        String basePackage = tableDTO.getBasePackage();// "com.kg.module";
        // 作者
        String author = tableDTO.getAuthor();// "ziro";
        /**
         * 说明：表名、主键类型、包名、前端view路径，必须是一对一的数组
         */
        // 表名
        String[] tableNames = new String[]{tableDTO.getTableName()};
        // 表主键类型（如：IdType.ASSIGN_UUID、IdType.ASSIGN_ID）
        IdType[] idTypes = new IdType[]{IdType.ASSIGN_UUID};
        // 包名
        String[] packages = new String[]{tableDTO.getTablePackage()};
        // 前端view路径
        String[] viewpaths = new String[]{tableDTO.getViewPath()};

        // ==================================开始执行生成=====================================
        start(basePath, basePackage, author, tableNames, idTypes, packages, viewpaths, tableDTO);
        // 打成压缩包
        String zipPath = basePath + ".zip";
        ZipUtil.zip(basePath, zipPath);
        // 打开输出文件夹
        try {
            RuntimeUtils.openDir(FilePathConfig.SAVE_PATH + "/generate/code/temp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回下载地址
        return FilePathConfig.switchUrl(zipPath);
    }

    // 代码生成器开始生成
    private void start(String basePath, String basePackage, String author, String[] tableNames,
                       IdType[] idTypes, String[] packages, String[] viewpaths, TableDTO tableDTO) {

        for (int i = 0; i < tableNames.length; i++) {
            // ===========================================执行生成=======================
            // 配置文件路径
            Map<OutputFile, String> pathInfo = new HashMap<>();
            // 后台和xml代码，输出到java
            String javaPath = basePath + "/java/src/main/java";
            pathInfo.put(OutputFile.xml, basePath + "/java/src/main/resources/mapper");
            // 前台代码，输出到vue
            pathInfo.put(OutputFile.indexVue, basePath + "/vue/src/views");
            // 权限脚本，输出到sql
            pathInfo.put(OutputFile.permissionSql, basePath + "/sql");
            // 配置生成器
            int finalIndex = i;
            FastAutoGenerator.create(dbUrl, dbUserName, dbPassword)
                    .globalConfig(builder -> {
                        builder.author(author) // 设置作者
                                .enableSwagger() // 开启swagger模式
                                .disableOpenDir()
                                .outputDir(javaPath); // 指定输出目录
                    })
                    .packageConfig(builder -> {
                        builder.parent(basePackage) // 设置父包名
                                .moduleName(packages[finalIndex]) // 设置父包模块名
                                // 设置各包路径
                                .entity("entity")
                                .dto("dto")
                                .dtoconvert("dto.convert")
                                .excelConstant("excels")
                                .excelOut("excels")
                                .service("service")
                                .serviceImpl("service")
                                .mapper("mapper")
                                .xml("mapper")
                                .controller("controller")
                                .pathInfo(pathInfo);
                    })
                    .strategyConfig(builder -> {
                        try {
                            builder.addInclude(tableNames[finalIndex])// 设置需要生成的表名
                                    .indexVueBuilder()// ===============indexVue配置
                                    .templateHtml(URLDecoder.decode(tableDTO.getTemplate(), "UTF-8"))
                                    .jsData(URLDecoder.decode(tableDTO.getJsData(), "UTF-8"))
                                    .jsCreated(URLDecoder.decode(tableDTO.getJsCreated(), "UTF-8"))
                                    .jsMethods(URLDecoder.decode(tableDTO.getJsMethods(), "UTF-8"))
                                    .templateCss(URLDecoder.decode(tableDTO.getCss(), "UTF-8"))
                                    .enableFileOverride()
                                    .viewPath(viewpaths[finalIndex])// 前端文件路径
                                    .permissionSQLBuilder()// ==========permissionSQL配置
                                    .enableFileOverride()
                                    .controllerBuilder()// =============controller配置
                                    .enableFileOverride()// 生成覆盖
                                    .enableRestStyle()// 开启RestController
                                    .serviceBuilder()// ================service配置
                                    .enableFileOverride()
                                    .mapperBuilder()// =================mapper配置
                                    .enableFileOverride()
                                    .dtoBuilder()// ====================DTO配置
                                    .enableFileOverride()
                                    .superClass(BaseDTO.class)
                                    .enableLombok()
                                    .excelsBuilder()// =================excel配置
                                    .enableFileOverride()
                                    .entityBuilder()// =================entity配置
                                    .enableFileOverride()
                                    .superClass(BaseEntity.class)
                                    .idType(idTypes[finalIndex])// 生成id类型
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

    // 生成表
    private void createTable(TableDTO tableDTO) throws BaseException {
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableDTO.getTableName() + ";");
        List<TableFieldDTO> fields = tableDTO.getFields();
        if (fields == null || fields.size() <= 0) {
            throw new BaseException("创建表失败！没有字段信息");
        }
        // 字段
        List<String> fieldsArr = new ArrayList<>();
        List<String> keysArr = new ArrayList<>();
        for (TableFieldDTO field : fields) {
            StringBuilder str = new StringBuilder();
            // 字段名
            str.append(" `" + field.getName() + "` ");
            // 字段类型和长度
            if (field.getType().equalsIgnoreCase("float")
                    || field.getType().equalsIgnoreCase("double")
                    || field.getType().equalsIgnoreCase("decimal")) {
                str.append(field.getType() + " (" + field.getLength() + "," + field.getPoint() + ") ");
            } else {
                str.append(field.getType() + " (" + field.getLength() + ") ");
            }
            // 是否必填
            if (field.isRequired()) {
                str.append(" NOT NULL ");
            } else {
                str.append(" NULL ");
            }
            // 注释
            if (StringUtils.hasText(field.getTitle())) {
                str.append(" COMMENT '" + field.getTitle() + "' ");
            }
            fieldsArr.add(str.toString());
            // 是否主键
            if (field.isKey()) {
                keysArr.add(" `" + field.getName() + "` ");
            }
        }
        // 自动配置时间字段
        fieldsArr.add("`create_time` datetime(0) NULL DEFAULT NULL COMMENT '添加时间'");
        fieldsArr.add("`update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间'");
        // 组装创建表sql
        StringBuilder createSql = new StringBuilder();
        createSql.append("CREATE TABLE ");
        createSql.append(tableDTO.getTableName());
        createSql.append(" ( ");
        createSql.append(fieldsArr.stream().collect(Collectors.joining(",")));
        if (keysArr.size() > 0) {
            createSql.append(",PRIMARY KEY ( ");
            createSql.append(keysArr.stream().collect(Collectors.joining(",")));
            createSql.append(" )");
        }
        createSql.append(" ) ");
        if (StringUtils.hasText(tableDTO.getTableDecription())) {
            createSql.append("COMMENT = '" + tableDTO.getTableDecription() + "';");
        }
        jdbcTemplate.execute(createSql.toString());
    }
}
