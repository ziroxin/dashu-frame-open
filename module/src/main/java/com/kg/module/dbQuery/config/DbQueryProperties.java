package com.kg.module.dbQuery.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 数据库查询配置属性
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.kg.db-query")
public class DbQueryProperties {

    /**
     * 模块总开关：false 时本模块所有 Bean 不注入
     */
    private Boolean enabled = true;

    /**
     * 是否允许写操作（INSERT/UPDATE/DELETE），仅开发管理员可用
     */
    private Boolean writeEnabled = false;

    /**
     * JDBC URL
     */
    private String url;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * 是否只读模式（仅约束查询接口 executeQuery）
     */
    private Boolean readOnly = true;

    /**
     * 默认数据库名
     */
    private String defaultDatabase;

    /**
     * 查询默认最大返回行数
     */
    private Integer defaultMaxRows = 1000;

    /**
     * 查询默认超时时间（秒）
     */
    private Integer defaultTimeout = 30;

    /**
     * 写预览允许的最大受影响行数，超过则拒绝执行
     */
    private Integer maxAffectedRows = 5000;

    /**
     * 写预览快照有效期（秒），二次确认须在此时间内完成
     */
    private Long snapshotTtlSeconds = 300L;
}
