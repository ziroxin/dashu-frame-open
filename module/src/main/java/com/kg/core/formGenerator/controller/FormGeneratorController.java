package com.kg.core.formGenerator.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.kg.component.file.FilePathConfig;
import com.kg.component.generator.util.RuntimeUtils;
import com.kg.component.utils.TimeUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.formGenerator.dto.TableDTO;
import com.kg.core.formGenerator.dto.TableFieldDTO;
import com.kg.core.formGenerator.dto.TableInfoDTO;
import com.kg.core.formGenerator.utils.GeneratorCodeUtils;
import com.kg.module.generator.entity.ZFormGenerator;
import com.kg.module.generator.service.ZFormGeneratorService;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
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
    @Resource
    private GeneratorCodeUtils generatorCodeUtils;
    @Resource
    private ZFormGeneratorService formGeneratorService;

    /**
     * 获取数据库名称
     */
    private String getMyDbName() {
        return generatorCodeUtils.getDbUrl().split("\\?")[0].split("/")[3];// 截取获得数据库名称
    }

    /**
     * 查询表列表
     */
    @GetMapping("generator/code/tableList")
    public List<TableInfoDTO> list() {
        // 查询所有表名列表
        String getAllTableNamesSql = "SELECT table_name tableName,table_comment tableComment " +
                "FROM information_schema.tables " +
                "WHERE table_schema = '" + getMyDbName() + "' ORDER BY table_name ASC";
        List<Map<String, Object>> tableList = jdbcTemplate.queryForList(getAllTableNamesSql);
        // 转成表信息实体
        return tableList.stream()
                .map(table -> JSONUtil.toBean(JSONUtil.toJsonStr(table), TableInfoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 查询表注释信息和字段列表
     */
    @GetMapping("generator/code/tableInfo")
    public List<TableFieldDTO> tableInfo(String tableName) {
        // 查询字段列表
        String getTableColumnsSql = "SELECT " +
                " column_name `name`,data_type type,character_maximum_length len1,numeric_precision len2, " +
                " numeric_scale point,is_nullable required,column_key `key`,column_comment title " +
                "FROM information_schema.columns " +
                "WHERE table_schema = '" + getMyDbName() + "' AND table_name = '" + tableName + "'";
        List<Map<String, Object>> columnList = jdbcTemplate.queryForList(getTableColumnsSql);
        return columnList.stream()
                .map(column -> {
                    TableFieldDTO bean = JSONUtil.toBean(JSONUtil.toJsonStr(column), TableFieldDTO.class);
                    bean.setLength(0);
                    if (column.get("type") != null && StringUtils.hasText(column.get("type").toString())) {
                        String[] noLengthType = {"text", "tinytext", "mediumtext", "longtext",
                                "blob", "tinyblob", "mediumblob", "longblob",
                                "date", "datetime", "timestamp", "time", "year"};
                        if (!Arrays.asList(noLengthType).contains(column.get("type").toString())) {
                            if (column.get("len1") != null && StringUtils.hasText(column.get("len1").toString())) {
                                bean.setLength(Integer.parseInt(column.get("len1").toString()));
                            } else if (column.get("len2") != null && StringUtils.hasText(column.get("len2").toString())) {
                                bean.setLength(Integer.parseInt(column.get("len2").toString()));
                            }
                        }
                    }
                    if (column.get("required") != null && StringUtils.hasText(column.get("required").toString())) {
                        bean.setRequired("NO".equals(column.get("required").toString()));
                    } else {
                        bean.setRequired(false);
                    }
                    if (column.get("key") != null && StringUtils.hasText(column.get("key").toString())) {
                        bean.setKey("PRI".equals(column.get("key").toString()));
                    } else {
                        bean.setKey(false);
                    }
                    return bean;
                })
                .collect(Collectors.toList());
    }

    /**
     * 检查表是否存在
     */
    @GetMapping("generator/code/hasTables")
    public boolean hasTables(String tableName) {
        return generatorCodeUtils.hasTables(tableName);
    }

    /**
     * 生成代码
     */
    @PostMapping("generator/code/byform")
    public String generate(@RequestBody TableDTO tableDTO) throws BaseException {
        if (tableDTO.getIsCoverTable()) {
            // 1.生成表
            createTable(tableDTO);
        }
        // 2.生成代码
        return generateCode(tableDTO);
    }

    // 生成代码
    private String generateCode(TableDTO tableDTO) {
        if (!FileUtil.isDirectory(FilePathConfig.SAVE_PATH)) {
            FileUtil.mkdir(FilePathConfig.SAVE_PATH);
        }
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
        LinkedList<String> tableNames = new LinkedList<>();
        tableNames.add(tableDTO.getTableName());
        // 表主键类型（如：IdType.ASSIGN_UUID、IdType.ASSIGN_ID）
        LinkedList<IdType> idTypes = new LinkedList<>();
        idTypes.add(IdType.ASSIGN_UUID);
        // 包名
        LinkedList<String> packages = new LinkedList<>();
        packages.add(tableDTO.getTablePackage());
        // 前端view路径
        LinkedList<String> viewPaths = new LinkedList<>();
        viewPaths.add(tableDTO.getViewPath());
        // 子表名List
        Map<String, Object> childTableMap = new HashMap<>();
        LinkedList<String> childTableList = new LinkedList<>();
        // 处理附件子表
        for (TableFieldDTO field : tableDTO.getFields()) {
            if (StringUtils.hasText(field.getChildFileTable())) {
                tableNames.add(field.getChildFileTable());// 子表名
                idTypes.add(IdType.ASSIGN_UUID);// 子表字段类型
                packages.add(StrUtil.toCamelCase(field.getChildFileTable()));// 子表包名
                viewPaths.add("");// 子表前端（不生成前端，所以置空）
                // 子表名，列表（驼峰）
                childTableList.add(StrUtil.toCamelCase(field.getChildFileTable()));
            }
        }
        childTableMap.put(tableDTO.getTableName(), childTableList);// 只有主表存储子表信息
        // ================================== 开始执行生成 =====================================
        generatorCodeUtils.start(basePath, "module", basePackage, author, "web-vue2",
                tableNames, idTypes, packages, viewPaths, tableDTO, childTableMap, tableDTO.getIsDeleteLogs());
        // 打成压缩包
        String zipPath = basePath + ".zip";
        ZipUtil.zip(basePath, zipPath);
        try {
            // 检测操作系统，window系统，则打开输出文件夹
            if (SystemUtils.IS_OS_WINDOWS) {
                RuntimeUtils.openDir(FilePathConfig.SAVE_PATH + "/generate/code/temp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 更新表单管理表生成状态
        formGeneratorService.lambdaUpdate().eq(ZFormGenerator::getFormId, tableDTO.getFormId())
                .set(ZFormGenerator::getStatus, "1")
                .set(ZFormGenerator::getUpdateTime, LocalDateTime.now()).update();
        // 返回下载地址
        return FilePathConfig.switchUrl(zipPath);
    }

    // 生成表
    private void createTable(TableDTO tableDTO) throws BaseException {
        // 检查表是否存在，若存在，则备份该表
        if (generatorCodeUtils.hasTables(tableDTO.getTableName())) {
            // 获取原表的创建语句
            String oldTblName = tableDTO.getTableName();
            String sql = jdbcTemplate.queryForMap("SHOW CREATE TABLE " + oldTblName).get("Create Table").toString();
            // 替换新表名
            String newTblName = oldTblName + "_bak_" + TimeUtils.now().toFormat("yyyyMMddHHmmss");
            sql = sql.replace("CREATE TABLE `" + oldTblName, "CREATE TABLE `" + newTblName);
            // 备份
            jdbcTemplate.execute(sql);
        }
        // 删除表
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableDTO.getTableName() + ";");
        List<TableFieldDTO> fields = tableDTO.getFields();
        if (fields == null || fields.size() <= 0) {
            throw new BaseException("创建表失败！没有字段信息");
        }
        // 字段
        List<String> fieldsArr = new ArrayList<>();
        List<String> keysArr = new ArrayList<>();
        for (TableFieldDTO field : fields) {
            if (!StringUtils.hasText(field.getName())) {
                // 无字段名，跳过（如行容器）
                continue;
            }
            if ("create_time".equals(field.getName()) || "update_time".equals(field.getName())) {
                throw new BaseException("生成表失败！create_time或update_time为生成表默认字段，不可在表单中配置，请修改字段名后重新生成！");
            }
            // 附件子表
            if (StringUtils.hasText(field.getChildFileTable())) {
                // 判断附件是否有此表
                if (generatorCodeUtils.hasTables(field.getChildFileTable())) {
                    // 获取原表的创建语句
                    String oldTblName = field.getChildFileTable();
                    String sql = jdbcTemplate.queryForMap("SHOW CREATE TABLE " + oldTblName).get("Create Table").toString();
                    // 替换新表名
                    String newTblName = oldTblName + "_bak_" + TimeUtils.now().toFormat("yyyyMMddHHmmss");
                    sql = sql.replace("CREATE TABLE `" + oldTblName, "CREATE TABLE `" + newTblName);
                    // 备份
                    jdbcTemplate.execute(sql);
                }
                // 删除表
                jdbcTemplate.execute("DROP TABLE IF EXISTS " + field.getChildFileTable() + ";");
                // 创建表
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `" + field.getChildFileTable() + "` (" +
                        "  `file_id` varchar(36) NOT NULL COMMENT '附件id'," +
                        "  `" + tableDTO.getTableName() + "_id` varchar(36) NULL COMMENT '主表id'," +
                        "  `file_url` varchar(100) NULL COMMENT '文件地址（文件访问地址）'," +
                        "  `file_old_name` varchar(200) NULL COMMENT '原文件名'," +
                        "  `file_name` varchar(100) NULL COMMENT '存储文件名'," +
                        "  `file_extend` varchar(20) NULL COMMENT '文件扩展名'," +
                        "  `file_size` bigint(20) NULL COMMENT '文件大小'," +
                        "  `order_index` int(10) DEFAULT NULL COMMENT '顺序'," +
                        "  `create_time` datetime(0) NULL COMMENT '附件上传时间'," +
                        "  PRIMARY KEY (`file_id`) USING BTREE" +
                        ") COMMENT = '" + tableDTO.getTableDecription() + "附件表';");
            } else {
                StringBuilder str = new StringBuilder();
                // 字段名
                str.append(" `" + field.getName() + "` ");
                // 字段类型和长度
                if (field.getType().equalsIgnoreCase("float")
                        || field.getType().equalsIgnoreCase("double")
                        || field.getType().equalsIgnoreCase("decimal")) {
                    str.append(field.getType() + " (" + field.getLength() + "," + field.getPoint() + ") ");
                } else {
                    if (field.getLength() > 0) {
                        str.append(field.getType() + " (" + field.getLength() + ") ");
                    } else {
                        str.append(field.getType() + " ");
                    }
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
        }
        // 自动配置时间字段
        fieldsArr.add("`create_time` datetime(0) NULL COMMENT '添加时间'");
        fieldsArr.add("`update_time` datetime(0) NULL COMMENT '修改时间'");
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
