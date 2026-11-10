package com.kg.module.dbQuery.controller;

import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.exception.BaseException;
import com.kg.module.dbQuery.config.DbQueryEnabled;
import com.kg.module.dbQuery.dto.*;
import com.kg.module.dbQuery.service.DbQueryService;
import com.kg.module.dbQuery.service.DbWriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 数据库查询控制器
 *
 * @author ziro
 * @date 2026/3/9
 */
@Slf4j
@Api(tags = "数据库查询工具")
@RestController
@RequestMapping("/db/query")
@DbQueryEnabled
public class DbQueryController {

    @Resource
    private DbQueryService dbQueryService;

    @Resource
    private DbWriteService dbWriteService;

    /**
     * 执行 SQL 查询
     */
    @ApiOperation(value = "/db/query/execute", notes = "执行SQL查询", httpMethod = "POST")
    @PostMapping("/execute")
    public DbQueryResultDTO executeQuery(@Valid @RequestBody DbQueryDTO dto) throws BaseException {
        try {
            return dbQueryService.executeQuery(dto);
        } catch (Exception e) {
            log.error("SQL执行失败", e);
            throw new BaseException("SQL执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据库列表
     */
    @ApiOperation(value = "/db/query/databases", notes = "获取数据库列表", httpMethod = "GET")
    @GetMapping("/databases")
    public List<DbDatabaseInfoDTO> getDatabases() throws BaseException {
        try {
            return dbQueryService.getDatabases();
        } catch (Exception e) {
            log.error("获取数据库列表失败", e);
            throw new BaseException("获取数据库列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取表列表
     */
    @ApiOperation(value = "/db/query/tables", notes = "获取表列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "databaseName", value = "数据库名", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/tables")
    public List<DbTableInfoDTO> getTables(@RequestParam(required = false) String databaseName) throws BaseException {
        try {
            return dbQueryService.getTables(databaseName);
        } catch (Exception e) {
            log.error("获取表列表失败", e);
            throw new BaseException("获取表列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取表结构
     */
    @ApiOperation(value = "/db/query/table/structure", notes = "获取表结构信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "databaseName", value = "数据库名", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "tableName", value = "表名", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/table/structure")
    public DbTableInfoDTO getTableStructure(
            @RequestParam(required = false) String databaseName,
            @RequestParam String tableName) throws BaseException {
        try {
            return dbQueryService.getTableStructure(databaseName, tableName);
        } catch (Exception e) {
            log.error("获取表结构失败", e);
            throw new BaseException("获取表结构失败: " + e.getMessage());
        }
    }

    /**
     * 导出查询结果为 CSV
     */
    @ApiOperation(value = "/db/query/export/csv", notes = "导出查询结果为CSV", httpMethod = "POST")
    @PostMapping("/export/csv")
    public void exportToCsv(@Valid @RequestBody DbQueryDTO dto, HttpServletResponse response) throws BaseException {
        try {
            String csv = dbQueryService.exportToCsv(dto);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=export.csv");

            // 添加 BOM 以支持 Excel 打开 UTF-8
            response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

            PrintWriter writer = response.getWriter();
            writer.write(csv);
            writer.flush();
        } catch (IOException e) {
            log.error("导出CSV失败", e);
            throw new BaseException("导出CSV失败: " + e.getMessage());
        }
    }

    /**
     * 获取模块状态（前端据此判断功能可用性与写模式）
     */
    @ApiOperation(value = "/db/query/status", notes = "获取模块状态", httpMethod = "GET")
    @GetMapping("/status")
    public DbQueryStatusDTO getStatus() {
        return dbQueryService.getStatus();
    }

    /**
     * 预览写 SQL（受影响行数 + 原始行备份 + 一次性token）
     */
    @ApiOperation(value = "/db/query/write/preview", notes = "预览写SQL", httpMethod = "POST")
    @PreAuthorize("hasAuthority('db-query-write-preview')")
    @AutoOperateLog(logMethod = "/db/query/write/preview", logMsg = "预览数据库写SQL")
    @PostMapping("/write/preview")
    public DbWritePreviewResultDTO previewWrite(@Valid @RequestBody DbWritePreviewDTO dto) throws BaseException {
        try {
            return dbWriteService.previewWrite(dto);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("写预览失败", e);
            throw new BaseException("写预览失败: " + e.getMessage());
        }
    }

    /**
     * 执行写 SQL（二次确认）
     */
    @ApiOperation(value = "/db/query/write/execute", notes = "执行写SQL", httpMethod = "POST")
    @PreAuthorize("hasAuthority('db-query-write-execute')")
    @AutoOperateLog(logMethod = "/db/query/write/execute", logMsg = "执行数据库写SQL")
    @PostMapping("/write/execute")
    public DbWriteResultDTO executeWrite(@Valid @RequestBody DbWriteExecuteDTO dto) throws BaseException {
        try {
            return dbWriteService.executeWrite(dto);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("写执行失败", e);
            throw new BaseException("写执行失败: " + e.getMessage());
        }
    }
}
