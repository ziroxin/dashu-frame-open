package com.kg.module.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * JDBC依赖的事务配置
 *
 * @author ziro
 * @date 2023-01-06 17:54:44
 * @see <a href="https://blog.csdn.net/u010963948/article/details/79208328">JDBC依赖的事务配置说明</a>
 */
public class JdbcTransactionalConfig implements TransactionManagementConfigurer {

    @Resource(name = "transactionManager")
    private PlatformTransactionManager transactionManager;

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }

    /**
     * 创建事务管理器bean
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}