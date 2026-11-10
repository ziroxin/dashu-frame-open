package com.kg.module.dbQuery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.dbQuery.entity.DbWriteAudit;
import org.springframework.stereotype.Repository;

/**
 * 数据库写操作审计 Mapper
 *
 * @author ziro
 * @date 2026/3/9
 */
@Repository
public interface DbWriteAuditMapper extends BaseMapper<DbWriteAudit> {
}
