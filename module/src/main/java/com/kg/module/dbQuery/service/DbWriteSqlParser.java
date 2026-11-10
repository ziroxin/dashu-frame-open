package com.kg.module.dbQuery.service;

import cn.hutool.core.util.StrUtil;
import com.kg.core.exception.BaseException;
import com.kg.module.dbQuery.dto.DbWriteSqlInfo;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 写 SQL（INSERT/UPDATE/DELETE）解析与校验器。
 * <p>
 * 基于 JSqlParser 将语句解析为 AST，只放行单条 INSERT/UPDATE/DELETE，
 * 强制 UPDATE/DELETE 带 WHERE，并拒绝多表/JOIN、子查询插入、堆叠语句及危险关键字。
 * 解析成功后为 UPDATE/DELETE 构造只读的预览 SQL（countSql / backupSql），
 * 表名与库名经标识符白名单校验后用反引号重建，避免二次拼接注入。
 * </p>
 *
 * @author ziro
 * @date 2026/3/9
 */
public final class DbWriteSqlParser {

    private DbWriteSqlParser() {
    }

    /**
     * 合法标识符（库名/表名/别名）：字母、数字、下划线
     */
    private static final Pattern IDENT_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    /**
     * 危险关键字黑名单（文件读写、时间盲注、DDL/DCL 等，任何写语句中都不允许出现）
     */
    private static final List<Pattern> DANGEROUS_PATTERNS = Arrays.asList(
            Pattern.compile("\\b(DROP|TRUNCATE|ALTER|CREATE|RENAME|GRANT|REVOKE)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bSHUTDOWN\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bSET\\s+GLOBAL\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(OUTFILE|DUMPFILE|LOAD_FILE)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bLOAD\\s+DATA\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bSLEEP\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bBENCHMARK\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(INTOFFLINE|INTO\\s+OFFLINE)\\b", Pattern.CASE_INSENSITIVE)
    );

    private static final Pattern JOIN_PATTERN = Pattern.compile("\\bJOIN\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern USING_PATTERN = Pattern.compile("\\bUSING\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_PATTERN = Pattern.compile("\\bSELECT\\b", Pattern.CASE_INSENSITIVE);

    /**
     * 解析并校验单条写 SQL。非法输入抛 BaseException。
     *
     * @param rawSql 原始 SQL
     * @return 解析结果（含类型、目标表、WHERE、预览 countSql/backupSql）
     */
    public static DbWriteSqlInfo parse(String rawSql) throws BaseException {
        if (StrUtil.isBlank(rawSql)) {
            throw new BaseException("SQL 不能为空");
        }

        String body = rawSql.trim();
        // 去除结尾分号
        while (body.endsWith(";")) {
            body = body.substring(0, body.length() - 1).trim();
        }
        if (StrUtil.isBlank(body)) {
            throw new BaseException("SQL 不能为空");
        }
        // 禁止堆叠语句
        if (body.indexOf(';') >= 0) {
            throw new BaseException("禁止多语句（堆叠）执行");
        }

        checkDangerous(body);

        Statement st;
        try {
            st = CCJSqlParserUtil.parse(body);
        } catch (Exception e) {
            throw new BaseException("SQL 解析失败: " + e.getMessage());
        }

        DbWriteSqlInfo info = new DbWriteSqlInfo();
        info.setRawSql(body);

        if (st instanceof Select) {
            throw new BaseException("查询语句请使用查询接口执行");
        } else if (st instanceof Insert) {
            handleInsert((Insert) st, info);
        } else if (st instanceof Update) {
            handleUpdate((Update) st, info);
        } else if (st instanceof Delete) {
            handleDelete((Delete) st, info);
        } else {
            throw new BaseException("仅允许执行 INSERT / UPDATE / DELETE 语句");
        }

        return info;
    }

    private static void handleInsert(Insert insert, DbWriteSqlInfo info) throws BaseException {
        // 拒绝 INSERT ... SELECT 及 VALUES 中的子查询：插入行数不可预览
        if (SELECT_PATTERN.matcher(insert.toString()).find()) {
            throw new BaseException("不支持 INSERT ... SELECT / 含子查询的插入");
        }
        info.setSqlType("INSERT");
        info.setTable(qualify(insert.getTable()));
    }

    private static void handleUpdate(Update update, DbWriteSqlInfo info) throws BaseException {
        Expression where = update.getWhere();
        if (where == null) {
            throw new BaseException("UPDATE 必须带 WHERE 条件");
        }
        String stmtStr = update.toString();
        if (JOIN_PATTERN.matcher(stmtStr).find() || multiTableBeforeSet(stmtStr)) {
            throw new BaseException("不支持多表 / JOIN 更新");
        }
        String from = fromClause(update.getTable());
        info.setSqlType("UPDATE");
        info.setTable(qualify(update.getTable()));
        info.setWhereClause(where.toString());
        info.setCountSql("SELECT COUNT(*) FROM " + from + " WHERE " + where);
        info.setBackupSql("SELECT * FROM " + from + " WHERE " + where);
    }

    private static void handleDelete(Delete delete, DbWriteSqlInfo info) throws BaseException {
        Expression where = delete.getWhere();
        if (where == null) {
            throw new BaseException("DELETE 必须带 WHERE 条件");
        }
        String stmtStr = delete.toString();
        if (JOIN_PATTERN.matcher(stmtStr).find() || USING_PATTERN.matcher(stmtStr).find()) {
            throw new BaseException("不支持多表 / JOIN / USING 删除");
        }
        String from = fromClause(delete.getTable());
        info.setSqlType("DELETE");
        info.setTable(qualify(delete.getTable()));
        info.setWhereClause(where.toString());
        info.setCountSql("SELECT COUNT(*) FROM " + from + " WHERE " + where);
        info.setBackupSql("SELECT * FROM " + from + " WHERE " + where);
    }

    // ==================== 私有辅助方法 ====================

    private static void checkDangerous(String sql) throws BaseException {
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(sql).find()) {
                throw new BaseException("SQL 包含危险操作，已被拦截");
            }
        }
    }

    /**
     * 全限定表名（反引号重建，标识符白名单校验）
     */
    private static String qualify(Table table) throws BaseException {
        String schema = unquote(table.getSchemaName());
        String name = unquote(table.getName());
        if (StrUtil.isBlank(name) || !IDENT_PATTERN.matcher(name).matches()) {
            throw new BaseException("非法的表名: " + table.getName());
        }
        if (schema != null) {
            if (!IDENT_PATTERN.matcher(schema).matches()) {
                throw new BaseException("非法的数据库名: " + table.getSchemaName());
            }
            return "`" + schema + "`.`" + name + "`";
        }
        return "`" + name + "`";
    }

    /**
     * FROM 子句片段（限定表名 + 可选别名）
     */
    private static String fromClause(Table table) throws BaseException {
        String qualified = qualify(table);
        Alias alias = table.getAlias();
        if (alias != null && StrUtil.isNotBlank(alias.getName())) {
            String aliasName = unquote(alias.getName());
            if (!IDENT_PATTERN.matcher(aliasName).matches()) {
                throw new BaseException("非法的表别名: " + alias.getName());
            }
            return qualified + " " + aliasName;
        }
        return qualified;
    }

    /**
     * 判断 UPDATE 的 SET 之前是否存在逗号（多表更新，如 UPDATE a, b SET ...）
     */
    private static boolean multiTableBeforeSet(String stmtStr) {
        int idx = stmtStr.toUpperCase().indexOf(" SET ");
        if (idx <= 0) {
            return false;
        }
        return stmtStr.substring(0, idx).indexOf(',') >= 0;
    }

    private static String unquote(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[`\"]", "").trim();
    }
}
