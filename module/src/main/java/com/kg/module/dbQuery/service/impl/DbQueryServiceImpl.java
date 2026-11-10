package com.kg.module.dbQuery.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zuser.entity.ZUser;
import com.kg.module.dbQuery.config.DbQueryEnabled;
import com.kg.module.dbQuery.config.DbQueryProperties;
import com.kg.module.dbQuery.dto.*;
import com.kg.module.dbQuery.service.DbQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 数据库查询服务实现
 *
 * @author ziro
 * @date 2026/3/9
 */
@Slf4j
@Service
@DbQueryEnabled
public class DbQueryServiceImpl implements DbQueryService {

    @Resource
    private DbQueryProperties dbQueryProperties;

    /**
     * 危险 SQL 黑名单模式（文件读写、时间盲注、堆叠、DDL/DCL）
     */
    private static final List<Pattern> DANGEROUS_PATTERNS = Arrays.asList(
            Pattern.compile("\\b(DROP)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(TRUNCATE)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(ALTER)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(CREATE)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(GRANT)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(REVOKE)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(LOCK)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(UNLOCK)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bSHUTDOWN\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bSET\\s+GLOBAL\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bOUTFILE\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bDUMPFILE\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bLOAD_FILE\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bLOAD\\s+DATA\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bEXECUTE\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bSLEEP\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bBENCHMARK\\s*\\(", Pattern.CASE_INSENSITIVE)
    );

    /**
     * 允许的 SQL 类型（只读）
     */
    private static final Set<String> ALLOWED_SQL_TYPES = new HashSet<>(Arrays.asList(
            "SELECT", "SHOW", "DESC", "DESCRIBE", "EXPLAIN"
    ));

    /**
     * 合法标识符（数据库名）白名单：仅字母、数字、下划线、$、-、.
     * 用于 setCatalog 等无法参数化的连接层入参校验
     */
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z0-9_$.-]+$");

    @PostConstruct
    public void init() {
        log.info("DbQueryService 初始化，只读模式: {}", dbQueryProperties.getReadOnly());
    }

    @Override
    public DbQueryResultDTO executeQuery(DbQueryDTO dto) throws BaseException {
        DbQueryResultDTO result = new DbQueryResultDTO();
        result.setSuccess(false);
        result.setColumns(new ArrayList<>());
        result.setColumnTypes(new ArrayList<>());
        result.setRows(new ArrayList<>());

        String sql = dto.getSql().trim();

        // 1. 检查 SQL 是否为空
        if (StrUtil.isBlank(sql)) {
            result.setErrorMessage("SQL 不能为空");
            return result;
        }

        // 2. 只读模式安全检查（类型白名单 + 危险词黑名单 + 禁止堆叠语句）
        if (dbQueryProperties.getReadOnly()) {
            checkDangerousSql(sql);
        }

        // 3. 执行查询
        long startTime = System.currentTimeMillis();
        try (Connection conn = getConnection(dto.getDatabaseName())) {
            if (dbQueryProperties.getReadOnly()) {
                conn.setReadOnly(true);
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(dto.getTimeout() != null ? dto.getTimeout() : dbQueryProperties.getDefaultTimeout());
                stmt.setMaxRows(dto.getMaxRows() != null ? dto.getMaxRows() : dbQueryProperties.getDefaultMaxRows());

                boolean hasResultSet = stmt.execute(sql);

                if (hasResultSet) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        parseResultSet(rs, result, dto.getMaxRows());
                    }
                } else {
                    result.setAffectedRows(stmt.getUpdateCount());
                }

                result.setSuccess(true);
            }
        } catch (SQLException e) {
            log.error("SQL 执行失败: {}", e.getMessage(), e);
            result.setErrorMessage("SQL 执行失败: " + e.getMessage());
        } finally {
            result.setExecuteTime(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    @Override
    public List<DbDatabaseInfoDTO> getDatabases() throws BaseException {
        List<DbDatabaseInfoDTO> databases = new ArrayList<>();

        try (Connection conn = getConnection(null);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW DATABASES")) {

            // 收集库名，稍后用参数化查询统计表数量，避免任何字符串拼接
            List<String> dbNames = new ArrayList<>();
            while (rs.next()) {
                dbNames.add(rs.getString("Database"));
            }

            String countSql = "SELECT COUNT(*) AS cnt FROM information_schema.tables WHERE table_schema = ?";
            for (String dbName : dbNames) {
                DbDatabaseInfoDTO db = new DbDatabaseInfoDTO();
                db.setDatabaseName(dbName);
                try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                    ps.setString(1, dbName);
                    try (ResultSet countRs = ps.executeQuery()) {
                        if (countRs.next()) {
                            db.setTableCount(countRs.getInt("cnt"));
                        }
                    }
                }
                databases.add(db);
            }
        } catch (SQLException e) {
            log.error("获取数据库列表失败: {}", e.getMessage(), e);
            throw new BaseException("获取数据库列表失败: " + e.getMessage());
        }

        return databases;
    }

    @Override
    public List<DbTableInfoDTO> getTables(String databaseName) throws BaseException {
        List<DbTableInfoDTO> tables = new ArrayList<>();
        String dbName = StrUtil.isNotBlank(databaseName) ? databaseName : getCurrentDatabase();

        String sql = "SELECT TABLE_NAME, TABLE_COMMENT, TABLE_TYPE, ENGINE, TABLE_ROWS " +
                     "FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? " +
                     "ORDER BY TABLE_NAME";

        try (Connection conn = getConnection(dbName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DbTableInfoDTO table = new DbTableInfoDTO();
                    table.setTableName(rs.getString("TABLE_NAME"));
                    table.setTableComment(rs.getString("TABLE_COMMENT"));
                    table.setTableType(rs.getString("TABLE_TYPE"));
                    table.setEngine(rs.getString("ENGINE"));
                    table.setTableRows(rs.getLong("TABLE_ROWS"));
                    tables.add(table);
                }
            }
        } catch (SQLException e) {
            log.error("获取表列表失败: {}", e.getMessage(), e);
            throw new BaseException("获取表列表失败: " + e.getMessage());
        }

        return tables;
    }

    @Override
    public DbTableInfoDTO getTableStructure(String databaseName, String tableName) {
        String dbName = StrUtil.isNotBlank(databaseName) ? databaseName : getCurrentDatabase();
        DbTableInfoDTO table = new DbTableInfoDTO();
        table.setTableName(tableName);

        // 获取列信息（走 JDBC DatabaseMetaData，驱动内部转义，非字符串拼接）
        table.setColumns(getColumns(dbName, tableName));

        // 获取索引信息（参数化查询 information_schema）
        table.setIndexes(getIndexes(dbName, tableName));

        // 获取表注释（参数化）
        String sql = "SELECT TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        try (Connection conn = getConnection(dbName);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dbName);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    table.setTableComment(rs.getString("TABLE_COMMENT"));
                }
            }
        } catch (SQLException e) {
            log.error("获取表结构失败: {}", e.getMessage(), e);
        }

        return table;
    }

    @Override
    public String exportToCsv(DbQueryDTO dto) throws BaseException {
        DbQueryResultDTO result = executeQuery(dto);

        if (!result.getSuccess()) {
            throw new BaseException(result.getErrorMessage());
        }

        StringBuilder csv = new StringBuilder();

        // 写入表头（表头也做公式注入防护）
        List<String> safeHeaders = new ArrayList<>();
        for (String col : result.getColumns()) {
            safeHeaders.add(sanitizeCsvCell(col));
        }
        csv.append(String.join(",", safeHeaders)).append("\n");

        // 写入数据行
        for (Map<String, Object> row : result.getRows()) {
            List<String> values = new ArrayList<>();
            for (String col : result.getColumns()) {
                Object val = row.get(col);
                String strVal = val == null ? "" : val.toString();
                // 公式注入防护 + CSV 分隔符转义
                strVal = escapeCsvCell(sanitizeCsvCell(strVal));
                values.add(strVal);
            }
            csv.append(String.join(",", values)).append("\n");
        }

        return csv.toString();
    }

    @Override
    public DbQueryStatusDTO getStatus() {
        DbQueryStatusDTO status = new DbQueryStatusDTO();
        status.setEnabled(true);
        status.setWriteEnabled(Boolean.TRUE.equals(dbQueryProperties.getWriteEnabled()));
        status.setReadOnly(Boolean.TRUE.equals(dbQueryProperties.getReadOnly()));
        ZUser currentUser = CurrentUserUtils.getCurrentUser();
        status.setDeveloper(currentUser != null && LoginConstant.isDeveloper(currentUser.getUserId()));
        return status;
    }

    // ==================== 私有方法 ====================

    /**
     * 只读模式安全检查
     */
    private void checkDangerousSql(String sql) throws BaseException {
        String upperSql = sql.toUpperCase().trim();

        // 1. 语句类型白名单
        boolean allowed = false;
        for (String type : ALLOWED_SQL_TYPES) {
            if (upperSql.startsWith(type)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            if (upperSql.startsWith("INSERT") || upperSql.startsWith("UPDATE") || upperSql.startsWith("DELETE")) {
                throw new BaseException("只读模式下不允许执行写操作");
            }
            throw new BaseException("不允许执行的 SQL 类型: " + upperSql.split("\\s+")[0]);
        }

        // 2. 禁止堆叠语句（去除末尾分号后，中间不得再出现分号）
        String trimmed = upperSql.endsWith(";") ? upperSql.substring(0, upperSql.length() - 1) : upperSql;
        if (trimmed.indexOf(';') >= 0) {
            throw new BaseException("只读模式下禁止多语句执行");
        }

        // 3. 危险关键词黑名单
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(sql).find()) {
                throw new BaseException("SQL 包含危险操作，已被拦截");
            }
        }
    }

    /**
     * 获取数据库连接
     * 注意：URL 固定来自配置，绝不用用户输入拼接 URL；切库统一走 setCatalog，
     * 消除 JDBC URL 注入（autoDeserialize / queryInterceptors 等参数污染）。
     * 非法库名直接拒绝，校验抛 SQLException 以避免受检异常逃逸到接口方法。
     */
    private Connection getConnection(String databaseName) throws SQLException {
        Connection conn = DriverManager.getConnection(
                dbQueryProperties.getUrl(),
                dbQueryProperties.getUsername(),
                dbQueryProperties.getPassword());
        if (StrUtil.isNotBlank(databaseName)) {
            if (databaseName.contains("..") || !IDENTIFIER_PATTERN.matcher(databaseName).matches()) {
                throw new SQLException("非法的数据库名称");
            }
            conn.setCatalog(databaseName);
        }
        return conn;
    }

    /**
     * 获取当前数据库名
     */
    private String getCurrentDatabase() {
        try (Connection conn = getConnection(null)) {
            return conn.getCatalog();
        } catch (SQLException e) {
            return dbQueryProperties.getDefaultDatabase();
        }
    }

    /**
     * 解析结果集
     */
    private void parseResultSet(ResultSet rs, DbQueryResultDTO result, Integer maxRows) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<String> columns = new ArrayList<>();
        List<String> columnTypes = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(metaData.getColumnLabel(i));
            columnTypes.add(metaData.getColumnTypeName(i));
        }
        result.setColumns(columns);
        result.setColumnTypes(columnTypes);

        List<Map<String, Object>> rows = new ArrayList<>();
        int rowCount = 0;
        int limit = maxRows != null ? maxRows : dbQueryProperties.getDefaultMaxRows();
        while (rs.next() && rowCount < limit) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(columns.get(i - 1), rs.getObject(i));
            }
            rows.add(row);
            rowCount++;
        }
        result.setRows(rows);
        result.setTotalRows(rowCount);
        result.setTruncated(rowCount >= limit && rs.next());
    }

    /**
     * 获取列信息（JDBC DatabaseMetaData，驱动内部转义）
     */
    private List<DbColumnInfoDTO> getColumns(String databaseName, String tableName) {
        List<DbColumnInfoDTO> columns = new ArrayList<>();

        try (Connection conn = getConnection(databaseName);
             ResultSet rs = conn.getMetaData().getColumns(null, databaseName, tableName, null)) {

            Set<String> primaryKeys = getPrimaryKeys(databaseName, tableName);

            while (rs.next()) {
                DbColumnInfoDTO col = new DbColumnInfoDTO();
                col.setColumnName(rs.getString("COLUMN_NAME"));
                col.setDataType(rs.getString("TYPE_NAME"));
                col.setColumnType(rs.getString("TYPE_NAME"));
                col.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                col.setDefaultValue(rs.getString("COLUMN_DEF"));
                col.setPrimaryKey(primaryKeys.contains(col.getColumnName()));
                col.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));
                col.setColumnComment(rs.getString("REMARKS"));
                col.setCharacterSet(rs.getString("CHAR_OCTET_LENGTH"));
                columns.add(col);
            }
        } catch (SQLException e) {
            log.error("获取列信息失败: {}", e.getMessage(), e);
        }

        return columns;
    }

    /**
     * 获取主键列
     */
    private Set<String> getPrimaryKeys(String databaseName, String tableName) {
        Set<String> primaryKeys = new HashSet<>();

        try (Connection conn = getConnection(databaseName);
             ResultSet rs = conn.getMetaData().getPrimaryKeys(null, databaseName, tableName)) {
            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            log.error("获取主键信息失败: {}", e.getMessage(), e);
        }

        return primaryKeys;
    }

    /**
     * 获取索引信息
     * 改用 information_schema.STATISTICS 参数化查询，替换原 SHOW INDEX 反引号拼接
     */
    private List<DbIndexInfoDTO> getIndexes(String databaseName, String tableName) {
        List<DbIndexInfoDTO> indexes = new ArrayList<>();
        Map<String, DbIndexInfoDTO> indexMap = new LinkedHashMap<>();

        String sql = "SELECT INDEX_NAME, NON_UNIQUE, INDEX_TYPE, COLUMN_NAME " +
                     "FROM information_schema.STATISTICS " +
                     "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                     "ORDER BY INDEX_NAME, SEQ_IN_INDEX";

        try (Connection conn = getConnection(databaseName);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, databaseName);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");

                    DbIndexInfoDTO index = indexMap.get(indexName);
                    if (index == null) {
                        index = new DbIndexInfoDTO();
                        index.setIndexName(indexName);
                        index.setNonUnique(rs.getInt("NON_UNIQUE") == 1);
                        index.setIndexType(rs.getString("INDEX_TYPE"));
                        index.setColumns(new ArrayList<>());
                        indexMap.put(indexName, index);
                    }

                    index.getColumns().add(rs.getString("COLUMN_NAME"));
                }
            }

            indexes.addAll(indexMap.values());
        } catch (SQLException e) {
            log.error("获取索引信息失败: {}", e.getMessage(), e);
        }

        return indexes;
    }

    /**
     * CSV 公式注入防护：以 = + @ 制表/回车开头，或 - 后跟非数字时，前置单引号
     */
    private String sanitizeCsvCell(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        char first = value.charAt(0);
        boolean dangerous = first == '=' || first == '+' || first == '@'
                || first == '\t' || first == '\r'
                || (first == '-' && (value.length() < 2 || !Character.isDigit(value.charAt(1))));
        return dangerous ? "'" + value : value;
    }

    /**
     * CSV 分隔符转义
     */
    private String escapeCsvCell(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
