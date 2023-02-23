package com.kg;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.kg.component.generator.FastAutoGenerator;
import com.kg.component.generator.config.OutputFile;
import com.kg.component.generator.engine.FreemarkerTemplateEngine;
import com.kg.component.generator.fill.Column;
import com.kg.core.base.dto.BaseDTO;
import com.kg.core.base.model.BaseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于Mybatis plus的代码生成器
 *
 * @author ziro
 * @date 2022/4/30 14:29
 * @see <a href="https://www.mybatis-plus.com/guide/generator-new.html#%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8">代码生成器入门</a>
 */
@SpringBootTest
public class MybatisPlusGenerator {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUserName;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Test
    public void generator() {
        // 输出目录
        String basePath = "E:/IdeaProjects/fwwbsyb/dashu-frame-open";
        // pom后台模块名（要和后台文件夹保持一致）
        String module = "module";
        // vue项目文件夹
        String vueFolder = "web-vue2";
        // 作者
        String author = "ziro";
        /**
         * 说明：表名、主键类型、包名、前端view路径，必须是一对一的数组
         */
        // 表名
        String[] tableNames = new String[]{"news"};
        // 表主键类型（如：IdType.ASSIGN_UUID、IdType.ASSIGN_ID）
        IdType[] idTypes = new IdType[]{IdType.ASSIGN_UUID};
        // 包名（支持多层包名，例如：system.role）
        String[] packages = new String[]{"news"};
        // 前端view路径（支持多层领，例如：/system/role）
        String[] viewpaths = new String[]{"/news"};

        // ==================================开始执行生成=====================================
        start(basePath, module, author, vueFolder, tableNames, idTypes, packages, viewpaths);
    }

    // 代码生成器开始生成
    private void start(String basePath, String module, String author, String vueFolder,
                       String[] tableNames, IdType[] idTypes, String[] packages, String[] viewpaths) {

        for (int i = 0; i < tableNames.length; i++) {
            // ===========================================执行生成=======================
            // 配置文件路径
            Map<OutputFile, String> pathInfo = new HashMap<>();
            pathInfo.put(OutputFile.xml, basePath + "/" + module + "/src/main/resources/mapper");
            pathInfo.put(OutputFile.indexVue, basePath + "/" + vueFolder + "/src/views");
            pathInfo.put(OutputFile.permissionSql, basePath + "/sql");
            // 配置生成器
            int finalIndex = i;
            FastAutoGenerator.create(dbUrl, dbUserName, dbPassword)
                    .globalConfig(builder -> {
                        builder.author(author) // 设置作者
                                .enableSwagger() // 开启swagger模式
                                .outputDir(basePath + "/" + module + "/src/main/java"); // 指定输出目录
                    })
                    .packageConfig(builder -> {
                        builder.parent("com.kg." + module) // 设置父包名
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
                        builder.addInclude(tableNames[finalIndex])// 设置需要生成的表名
                                .indexVueBuilder()// ===============indexVue配置
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
//                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                    })
                    .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                    .execute();
        }
    }
}
