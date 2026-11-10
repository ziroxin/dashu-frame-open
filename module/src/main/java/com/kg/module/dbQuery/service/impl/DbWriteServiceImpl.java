package com.kg.module.dbQuery.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zuser.entity.ZUser;
import com.kg.module.dbQuery.config.DbQueryEnabled;
import com.kg.module.dbQuery.config.DbQueryProperties;
import com.kg.module.dbQuery.dto.*;
import com.kg.module.dbQuery.entity.DbWriteAudit;
import com.kg.module.dbQuery.mapper.DbWriteAuditMapper;
import com.kg.module.dbQuery.service.DbWriteService;
import com.kg.module.dbQuery.service.DbWriteSqlParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 数据库写操作服务实现。
 * <p>
 * 流程（方案A）：previewWrite 校验并只读预览受影响行数 + 原始行样本，生成一次性 token 存 Redis；
 * executeWrite 校验 token/用户/SQL 一致性后，在事务中执行 DML，做行数上限与"不超过预览行数"保护，
 * 超限自动回滚，最终写审计。全程仅开发管理员可用，且受 com.kg.db-query.write-enabled 开关约束。
 * </p>
 *
 * @author ziro
 * @date 2026/3/9
 */
@Slf4j
@Service
@DbQueryEnabled
public class DbWriteServiceImpl implements DbWriteService {

    @Resource
    private DbQueryProperties dbQueryProperties;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private DbWriteAuditMapper dbWriteAuditMapper;

    /**
     * 合法标识符（数据库名）白名单，用于 setCatalog 入参校验
     */
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z0-9_$.-]+$");

    private static final String SNAPSHOT_KEY_PREFIX = "db-write:snapshot:";

    @Override
    public DbWritePreviewResultDTO previewWrite(DbWritePreviewDTO dto) throws BaseException {
        DbWritePreviewResultDTO result = new DbWritePreviewResultDTO();
        result.setSuccess(false);
        result.setColumns(new ArrayList<>());
        result.setColumnTypes(new ArrayList<>());
        result.setRows(new ArrayList<>());

        // 1. 门控：写开关 + 开发管理员
        ZUser currentUser = requireDeveloper();

        // 2. 解析校验
        DbWriteSqlInfo info = DbWriteSqlParser.parse(dto.getSql());
        result.setSqlType(info.getSqlType());
        result.setTable(info.getTable());

        int maxBackup = dto.getMaxBackupRows() != null && dto.getMaxBackupRows() > 0
                ? dto.getMaxBackupRows() : 200;
        long startTime = System.currentTimeMillis();

        // 3. 只读预览：统计行数 + 备份原始行
        try (Connection conn = getConnection(dto.getDatabaseName())) {
            conn.setReadOnly(true);
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(dbQueryProperties.getDefaultTimeout());

                if (!"INSERT".equals(info.getSqlType())) {
                    try (ResultSet rs = stmt.executeQuery(info.getCountSql())) {
                        if (rs.next()) {
                            result.setAffectedRows(rs.getInt(1));
                        }
                    }

                    stmt.setMaxRows(maxBackup);
                    try (ResultSet rs = stmt.executeQuery(info.getBackupSql())) {
                        parseRows(rs, result);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("写预览失败: {}", e.getMessage(), e);
            result.setErrorMessage("写预览失败: " + e.getMessage());
            return result;
        } finally {
            result.setExecuteTime(System.currentTimeMillis() - startTime);
        }

        // 4. 生成一次性 token，暂存快照
        String token = IdUtil.fastSimpleUUID();
        String sqlHash = md5(info.getRawSql());

        DbWriteSnapshot snapshot = new DbWriteSnapshot();
        snapshot.setSql(info.getRawSql());
        snapshot.setSqlType(info.getSqlType());
        snapshot.setDatabaseName(resolveDatabase(dto.getDatabaseName()));
        snapshot.setAffectedRows(result.getAffectedRows());
        snapshot.setUserId(currentUser.getUserId());
        snapshot.setSqlHash(sqlHash);
        snapshot.setCreateTime(System.currentTimeMillis());

        Long ttl = dbQueryProperties.getSnapshotTtlSeconds();
        redisUtils.set(SNAPSHOT_KEY_PREFIX + token, JSONUtil.toJsonStr(snapshot), ttl);

        result.setToken(token);
        result.setExpireSeconds(ttl);
        result.setSuccess(true);
        result.setMessage("INSERT".equals(info.getSqlType())
                ? "预览完成，请确认后执行（插入语句不统计受影响行数）"
                : "预览完成，将影响 " + result.getAffectedRows() + " 行，请确认后执行");
        return result;
    }

    @Override
    public DbWriteResultDTO executeWrite(DbWriteExecuteDTO dto) throws BaseException {
        DbWriteResultDTO result = new DbWriteResultDTO();
        result.setSuccess(false);

        // 1. 门控
        ZUser currentUser = requireDeveloper();

        // 2. 取回快照
        String key = SNAPSHOT_KEY_PREFIX + dto.getToken();
        Object cached = redisUtils.get(key);
        if (cached == null) {
            throw new BaseException("预览已过期或不存在，请重新预览后再执行");
        }
        DbWriteSnapshot snapshot = JSONUtil.toBean(cached.toString(), DbWriteSnapshot.class);

        // 3. 一致性校验：同一用户 + SQL 与预览一致
        if (!StrUtil.equals(snapshot.getUserId(), currentUser.getUserId())) {
            throw new BaseException("确认token与当前用户不匹配");
        }
        String normalized = normalize(dto.getSql());
        if (!md5(normalized).equals(snapshot.getSqlHash())) {
            throw new BaseException("执行的 SQL 与预览时不一致，请重新预览");
        }

        // 4. 重新解析校验（纵深防御）
        DbWriteSqlInfo info = DbWriteSqlParser.parse(dto.getSql());

        Integer previewRows = snapshot.getAffectedRows();
        int maxAffected = dbQueryProperties.getMaxAffectedRows();
        long startTime = System.currentTimeMillis();

        Connection conn = null;
        boolean committed = false;
        try {
            conn = getConnection(snapshot.getDatabaseName());
            conn.setAutoCommit(false);
            int actual;
            try (Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(dbQueryProperties.getDefaultTimeout());
                stmt.execute(info.getRawSql());
                actual = stmt.getUpdateCount();
            }

            // 5. 行数保护
            if (actual > maxAffected) {
                conn.rollback();
                String msg = "实际影响行数(" + actual + ")超过允许上限(" + maxAffected + ")，已回滚";
                writeAudit(currentUser, snapshot, info, actual, false, msg, dto.getToken());
                throw new BaseException(msg);
            }
            if (previewRows != null && actual > previewRows) {
                conn.rollback();
                String msg = "实际影响行数(" + actual + ")超过预览(" + previewRows + ")，数据已变化，已回滚";
                writeAudit(currentUser, snapshot, info, actual, false, msg, dto.getToken());
                throw new BaseException(msg);
            }

            conn.commit();
            committed = true;
            redisUtils.delete(key);

            writeAudit(currentUser, snapshot, info, actual, true, "执行成功", dto.getToken());

            result.setSuccess(true);
            result.setAffectedRows(actual);
            result.setMessage(info.getSqlType() + " 执行成功，影响 " + actual + " 行");
        } catch (SQLException e) {
            safeRollback(conn);
            writeAudit(currentUser, snapshot, info, null, false, "执行失败: " + e.getMessage(), dto.getToken());
            log.error("写执行失败: {}", e.getMessage(), e);
            throw new BaseException("写执行失败: " + e.getMessage());
        } catch (BaseException e) {
            throw e;
        } catch (RuntimeException e) {
            if (!committed) {
                safeRollback(conn);
            }
            writeAudit(currentUser, snapshot, info, null, false, "执行异常: " + e.getMessage(), dto.getToken());
            log.error("写执行异常: {}", e.getMessage(), e);
            throw new BaseException("写执行异常: " + e.getMessage());
        } finally {
            closeQuietly(conn);
            result.setExecuteTime(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 门控：写功能开关 + 当前用户必须为开发管理员
     */
    private ZUser requireDeveloper() throws BaseException {
        if (!Boolean.TRUE.equals(dbQueryProperties.getWriteEnabled())) {
            throw new BaseException("写操作功能未开启，请联系管理员");
        }
        ZUser currentUser = CurrentUserUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BaseException("未获取到当前登录用户");
        }
        if (!LoginConstant.isDeveloper(currentUser.getUserId())) {
            throw new BaseException("仅开发管理员可执行数据库写操作");
        }
        return currentUser;
    }

    /**
     * 解析数据库名（缺省回退到配置默认库）
     */
    private String resolveDatabase(String databaseName) {
        return StrUtil.isNotBlank(databaseName) ? databaseName : dbQueryProperties.getDefaultDatabase();
    }

    /**
     * 获取数据库连接（URL 固定来自配置，切库走 setCatalog，非法库名拒绝）
     */
    private Connection getConnection(String databaseName) throws SQLException {
        Connection conn = DriverManager.getConnection(
                dbQueryProperties.getUrl(),
                dbQueryProperties.getUsername(),
                dbQueryProperties.getPassword());
        if (StrUtil.isNotBlank(databaseName)) {
            if (databaseName.contains("..") || !IDENTIFIER_PATTERN.matcher(databaseName).matches()) {
                conn.close();
                throw new SQLException("非法的数据库名称");
            }
            conn.setCatalog(databaseName);
        }
        return conn;
    }

    private void parseRows(ResultSet rs, DbWritePreviewResultDTO result) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        List<String> columns = new ArrayList<>();
        List<String> types = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            columns.add(meta.getColumnLabel(i));
            types.add(meta.getColumnTypeName(i));
        }
        result.setColumns(columns);
        result.setColumnTypes(types);

        List<Map<String, Object>> rows = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= count; i++) {
                row.put(columns.get(i - 1), toDisplayValue(rs.getObject(i)));
            }
            rows.add(row);
        }
        result.setRows(rows);
    }

    /**
     * 将 JDBC 值转成可稳定 JSON 序列化的展示值
     */
    private Object toDisplayValue(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof byte[]) {
            byte[] bytes = (byte[]) val;
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        }
        if (val instanceof Timestamp || val instanceof java.util.Date
                || val instanceof java.time.temporal.Temporal || val instanceof java.time.LocalDate) {
            return val.toString();
        }
        return val;
    }

    private void writeAudit(ZUser user, DbWriteSnapshot snapshot, DbWriteSqlInfo info,
                            Integer affected, boolean success, String message, String token) {
        try {
            DbWriteAudit audit = new DbWriteAudit();
            audit.setUserId(user.getUserId());
            audit.setUsername(user.getUserName());
            audit.setDatabaseName(snapshot.getDatabaseName());
            audit.setSqlType(info.getSqlType());
            audit.setSqlText(truncate(info.getRawSql(), 5000));
            audit.setPreviewRows(snapshot.getAffectedRows());
            audit.setAffectedRows(affected);
            audit.setSuccess(success);
            audit.setMessage(truncate(message, 1000));
            audit.setSnapshotToken(token);
            audit.setCreateTime(LocalDateTime.now());
            dbWriteAuditMapper.insert(audit);
        } catch (Exception e) {
            log.error("写操作审计记录失败: {}", e.getMessage(), e);
        }
    }

    private void safeRollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {
                log.warn("回滚失败: {}", ignore.getMessage());
            }
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignore) {
                // ignore
            }
        }
    }

    private String normalize(String sql) {
        String body = sql == null ? "" : sql.trim();
        while (body.endsWith(";")) {
            body = body.substring(0, body.length() - 1).trim();
        }
        return body;
    }

    private String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return null;
        }
        return text.length() <= max ? text : text.substring(0, max);
    }
}
