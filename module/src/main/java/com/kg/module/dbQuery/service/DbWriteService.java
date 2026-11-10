package com.kg.module.dbQuery.service;

import com.kg.core.exception.BaseException;
import com.kg.module.dbQuery.dto.DbWriteExecuteDTO;
import com.kg.module.dbQuery.dto.DbWritePreviewDTO;
import com.kg.module.dbQuery.dto.DbWritePreviewResultDTO;
import com.kg.module.dbQuery.dto.DbWriteResultDTO;

/**
 * 数据库写操作服务（仅开发管理员，预览-确认-执行 + 审计）
 *
 * @author ziro
 * @date 2026/3/9
 */
public interface DbWriteService {

    /**
     * 预览写 SQL：校验语句、统计受影响行数、备份原始行样本，生成一次性确认token
     *
     * @param dto 预览请求
     * @return 预览结果
     */
    DbWritePreviewResultDTO previewWrite(DbWritePreviewDTO dto) throws BaseException;

    /**
     * 执行写 SQL：校验token与SQL一致性，事务执行并做行数保护，超限回滚，写审计
     *
     * @param dto 执行请求
     * @return 执行结果
     */
    DbWriteResultDTO executeWrite(DbWriteExecuteDTO dto) throws BaseException;
}
