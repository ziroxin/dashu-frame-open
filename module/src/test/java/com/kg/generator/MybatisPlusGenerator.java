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
 * @see <a href="https://baomidou.com/guides/new-code-generator/">代码生成器入门</a>
 */
@SpringBootTest
public class MybatisPlusGenerator {
    @Resource
    private GeneratorCodeUtils generatorCodeUtils;

    @Test
    public void generator() {
        // ========= 1 基础配置 =========
        // 输出目录（当前项目根目录）例如：E:/IdeaProjects/fwwbsyb/dashu-frame-open
        String basePath = System.getProperty("user.dir").replace(File.separator, "/").replace("/module", "");
        // pom后台模块名（要和后台文件夹保持一致）
        String module = "module";
        String basePackage = "com.kg." + module;
        // vue项目文件夹
        String vueFolder = "web-vue2";
        String vue3Folder = "web-vue3";
        // 作者
        String author = "ziro";

        // ========= 2 表、包、路径配置 =========
        // 说明：允许同时生成多个表的代码；
        //      注意 [表名、主键类型、包名、前端view路径] 必须是一一对应的LinkedList，按顺序add
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

        // ========= 3 删除日志配置 =========
        // 说明： 1. 日志表名，必须是[表名_logs]；
        //       2. 日志表字段包含全部主表字段，并增加2个字段：主键[logs_id]、删除时间[delete_time]
        //       3. [删除日志表]不要手动创建，由代码生成器自动生成（若存在同名表，会自动备份原表，然后生成新表）
        // 是否生成删除日志
        boolean isDeleteLog = true;

        // ======== 4 执行代码生成 =========
        generatorCodeUtils.start(basePath, module, basePackage, author, vueFolder, vue3Folder,
                tableNames, idTypes, packages, viewPaths, null, null, isDeleteLog);
    }
}
