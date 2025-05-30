package com.kg.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.kg.core.formGenerator.utils.GeneratorCodeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedList;

/**
 * 基于Mybatis plus的代码生成器
 *
 * @author ziro
 * @date 2022/4/30 14:29
 * @see <a href="https://baomidou.com/pages/779a6e/#%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8">代码生成器入门</a>
 */
@SpringBootTest
public class MybatisPlusGenerator {
    @Resource
    private GeneratorCodeUtils generatorCodeUtils;

    @Test
    public void generator() {
        // 输出目录（当前项目根目录）例如：E:/IdeaProjects/fwwbsyb/dashu-frame-open
        String basePath = System.getProperty("user.dir").replace(File.separator, "/").replace("/module", "");
        // pom后台模块名（要和后台文件夹保持一致）
        String module = "module";
        String basePackage = "com.kg." + module;
        // vue项目文件夹
        String vueFolder = "web-vue2";
        // 作者
        String author = "ziro";
        /**
         * 说明：允许同时生成多个表的代码，但需要注意表名、主键类型、包名、前端view路径，必须是一一对应的LinkedList，按顺序add
         */
        // 表名，必填
        LinkedList<String> tableNames = new LinkedList<>();
        tableNames.add("a_test");
        // 表主键类型，必填（如：IdType.ASSIGN_UUID、IdType.ASSIGN_ID）
        LinkedList<IdType> idTypes = new LinkedList<>();
        idTypes.add(IdType.ASSIGN_UUID);
        // 包名，必填（支持多层包名，例如：system.role）
        LinkedList<String> packages = new LinkedList<>();
        packages.add("atest");
        // 前端view路径，非必填（支持多层目录，例如：/system/role）
        LinkedList<String> viewPaths = new LinkedList<>();
        viewPaths.add("/atest");// 允许为空，若为空则不生成前端页面

        // ==================================开始执行生成=====================================
        generatorCodeUtils.start(basePath, module, basePackage, author, vueFolder,
                tableNames, idTypes, packages, viewPaths, null, null, true);
    }
}
