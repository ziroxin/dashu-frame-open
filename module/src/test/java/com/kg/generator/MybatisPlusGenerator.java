package com.kg.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.kg.core.formGenerator.dto.TableDTO;
import com.kg.core.formGenerator.utils.GeneratorCodeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
        //      注意 [表名、主键类型、包名、前端view路径、删除日志、附件] 必须是一一对应的LinkedList，按顺序add
        // 表名，必填
        LinkedList<String> tableNames = new LinkedList<>();
        tableNames.add("a_table");
//        tableNames.add("a_table");// 多表生成时，用多次add添加

        // 表主键类型，必填（如：IdType.ASSIGN_UUID、IdType.ASSIGN_ID）
        LinkedList<IdType> idTypes = new LinkedList<>();
        idTypes.add(IdType.ASSIGN_UUID);
//        idTypes.add(IdType.ASSIGN_UUID);// 多表生成时，用多次add添加

        // 包名，必填（支持多层包名，例如：system.role）
        LinkedList<String> packages = new LinkedList<>();
        packages.add("atable");
//        packages.add("atable");// 多表生成时，用多次add添加

        // 前端view路径，非必填（支持多层目录，例如：/system/role）
        LinkedList<String> viewPaths = new LinkedList<>();
        viewPaths.add("/atable");// 允许为空，若为空则不生成前端页面
//        viewPaths.add("/atable");// 多表生成时，用多次add添加

        // ========= 3 删除日志配置 =========
        // 说明： 1. 日志表名，自动生成[主表名_logs]；
        //       2. 日志表字段包含全部主表字段，并增加2个字段：主键[logs_id]、删除时间[delete_time]
        //       3. [删除日志表]不要手动创建，由代码生成器自动生成（若存在同名表，会自动备份原表，然后生成新表）
        // 配置：是否生成删除日志
        LinkedList<Boolean> isDeleteLogs = new LinkedList<>();
        isDeleteLogs.add(true);
//        isDeleteLogs.add(true);// 多表生成时，用多次add添加

        // ========= 4 附件表配置 =========
        // 说明： 1. 附件表名，自动生成[主表名_files]；
        //       2. [附件表]不要手动创建，由代码生成器自动生成（若存在同名表，会自动备份原表，然后生成新表）
        //       3. 附件表关联主表的字段名，自动生成[主表名_id]
        // 配置：是否生成附件表
        LinkedList<Boolean> isAttachments = new LinkedList<>();
        isAttachments.add(false);
//        isAttachments.add(true);// 多表生成时，用多次add添加


        // ========= 5 配置【查询字段、表格字端、导入字段、导出字段】信息 =========
        // 说明： 1. map的key是表名，value是逗号分隔的字段名；
        //       2. 若为空，则按照默认规则生成；
        //       3. 若不为空，则按照配置规则生成；
        // 配置：查询字段
        Map<String, String> searchMap = new HashMap<>(); // 查询字段
        searchMap.put("a_table", "mobile");
        // 配置：表格字段
        Map<String, String> listMap = new HashMap<>(); // 表格字段
        listMap.put("a_table", "mobile,order_index,field116,a,b,c,d");
        // 配置：导入字段
        Map<String, String> importMap = new HashMap<>(); // 导入字段
        importMap.put("a_table", "mobile,field116,a,b,c,d");
        // 配置：导出字段
        Map<String, String> exportMap = new HashMap<>(); // 导出字段
        exportMap.put("a_table", "mobile,field116,a,b,c,d");


        /*                            上方代码，可修改配置生成信息                           */
        /* ----------------------------------- 分割线 ----------------------------------- */
        /*                                下方代码，无需修改                                */


        // ========= 6 检查配置，并准备生成 =========
        if (!checkArraysLength(tableNames, idTypes, packages, viewPaths, isDeleteLogs, isAttachments)) {
            throw new RuntimeException("配置项数组长度不一致，请检查！！！");
        }
        // 生成附件表，并自动处理附件相关信息（不要改动）
        Map<String, Object> childTableMap = null;// 子表信息
        Map<String, String> attachmentFieldMap = null;// 配置子表的前端生成需要的信息
        TableDTO tableDTO = null;// 生成前端代码所需信息
        if (isAttachments.contains(true)) {
            childTableMap = new HashMap<>();
            attachmentFieldMap = new HashMap<>();
            tableDTO = new TableDTO();
            long lens = tableNames.size();
            for (int i = 0; i < lens; i++) {
                if (isAttachments.get(i)) {
                    // 主表名
                    String tableName = tableNames.get(i);
                    // 附件表名
                    String childTableName = tableName + "_files";
                    String childCamelTableName = StrUtil.toCamelCase(childTableName);
                    // 生成附件的数据库表
                    generatorCodeUtils.createAttachmentTable(tableName, childTableName);
                    // 附件子表代码生成配置
                    tableNames.add(childTableName);// 子表名
                    idTypes.add(IdType.ASSIGN_UUID);// 子表字段类型
                    packages.add(childCamelTableName);// 子表包名
                    viewPaths.add("");// 子表前端（不生成前端，所以置空）
                    isDeleteLogs.add(false);// 父表有删除日志，则附件子表也有删除日志
                    isAttachments.add(false);// 子表不生成附件表
                    // 给主表添加子表配置（子表名）
                    LinkedList<String> childTableList = new LinkedList<>();// 子表名，多个子表支持配置列表（驼峰）
                    childTableList.add(childCamelTableName);
                    childTableMap.put(tableName, childTableList);
                    // 配置子表的前端生成需要的信息
                    attachmentFieldMap.put(tableName, childCamelTableName.toLowerCase() + "List");
                    // 给主表的日志代码，加上附件信息
                    if (isDeleteLogs.get(i)) {
                        childTableMap.put(tableName + "_logs", childTableList);
                        attachmentFieldMap.put(tableName + "_logs", childCamelTableName.toLowerCase() + "List");
                    }
                }
            }
            tableDTO.setAttachmentField(attachmentFieldMap);
        }
        // 自动配置字段信息
        if (!searchMap.isEmpty() || !listMap.isEmpty() || !importMap.isEmpty() || !exportMap.isEmpty()) {
            if (tableDTO == null) {
                tableDTO = new TableDTO();
            }
            tableDTO.setSearchMap(searchMap);
            tableDTO.setListMap(listMap);
            tableDTO.setImportMap(importMap);
            tableDTO.setExportMap(exportMap);
        }
        // 标记在本地Test生成代码，而非网页在线代码生成器
        tableDTO.setGenerateType("code");

        // ========= 7 开始执行代码生成 =========
        generatorCodeUtils.start(basePath, module, basePackage, author, vueFolder, vue3Folder,
                tableNames, idTypes, packages, viewPaths, tableDTO, childTableMap, isDeleteLogs);
    }

    /**
     * 检查数组长度是否一致
     *
     * @param arrays 数组
     * @return true：一致；false：不一致
     */
    private boolean checkArraysLength(LinkedList... arrays) {
        for (LinkedList arr : arrays) {
            if (arr.size() != arrays[0].size()) {
                return false;
            }
        }
        return true;
    }
}
