package com.kg.module.dbQuery.service;

import com.kg.core.exception.BaseException;
import com.kg.module.dbQuery.dto.*;

import java.util.List;

/**
 * 数据库查询服务接口
 *
 * @author ziro
 * @date 2026/3/9
 */
public interface DbQueryService {

    /**
     * 执行 SQL 查询
     *
     * @param dto 查询请求
     * @return 查询结果
     */
    DbQueryResultDTO executeQuery(DbQueryDTO dto) throws BaseException;

    /**
     * 获取所有数据库列表
     *
     * @return 数据库列表
     */
    List<DbDatabaseInfoDTO> getDatabases() throws BaseException;

    /**
     * 获取指定数据库的所有表
     *
     * @param databaseName 数据库名
     * @return 表列表
     */
    List<DbTableInfoDTO> getTables(String databaseName) throws BaseException;

    /**
     * 获取表结构信息
     *
     * @param databaseName 数据库名
     * @param tableName    表名
     * @return 表信息
     */
    DbTableInfoDTO getTableStructure(String databaseName, String tableName);

    /**
     * 导出查询结果为 CSV
     *
     * @param dto 查询请求
     * @return CSV 内容
     */
    String exportToCsv(DbQueryDTO dto) throws BaseException;

    /**
     * 获取模块状态（前端据此判断功能可用性与写模式）
     *
     * @return 状态信息
     */
    DbQueryStatusDTO getStatus();
}
