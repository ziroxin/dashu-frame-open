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
        // 指定输出目录==========改成自己的目录
        String basePath = "E:/IdeaProjects/fwwbsyb/dashu-frame-open";
        // pom模块名
        String module = "module";
        // 作者
        String author = "ziro";
        // 表名
        String[] dataTableName = new String[]{"a_test"};
        // 包名
        String[] dataPackage = new String[]{"atest"};
        // 前端view路径
        String[] dataViewpath = new String[]{"/atest"};
        // 开始执行生成
        start(basePath, module, author, dataTableName, dataPackage, dataViewpath);

    }

    // 代码生成器开始生成
    private void start(String basePath, String module, String author,
                       String[] dataTableName, String[] dataPackage, String[] dataViewpath) {

        for (int i = 0; i < dataTableName.length; i++) {
            // ===========================================执行生成=======================
            // 配置文件路径
            Map<OutputFile, String> pathInfo = new HashMap<>();
            pathInfo.put(OutputFile.xml, basePath + "/" + module + "/src/main/resources/mapper");
            pathInfo.put(OutputFile.indexVue, basePath + "/web-vue2/src/views");
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
                                .moduleName(dataPackage[finalIndex]) // 设置父包模块名
                                // 设置各包路径
                                .entity("entity")
                                .dto("dto")
                                .dtoconvert("dto.convert")
                                .service("service")
                                .serviceImpl("service")
                                .mapper("mapper")
                                .xml("mapper")
                                .controller("controller")
                                .pathInfo(pathInfo);
                    })
                    .strategyConfig(builder -> {
                        builder.addInclude(dataTableName[finalIndex])// 设置需要生成的表名
                                .indexVueBuilder()// ===============indexVue配置
                                .enableFileOverride()
                                .viewPath(dataViewpath[finalIndex])// 前端文件路径
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
                                .entityBuilder()// =================entity配置
                                .enableFileOverride()
                                .superClass(BaseEntity.class)
                                .idType(IdType.ASSIGN_UUID)// 生成id类型
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
