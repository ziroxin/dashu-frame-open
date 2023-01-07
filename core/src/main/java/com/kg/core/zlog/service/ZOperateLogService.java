package com.kg.core.zlog.service;

import com.kg.core.zlog.entity.ZOperateLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-01-07
 */
public interface ZOperateLogService extends IService<ZOperateLog> {

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    String exportExcel(String params);
}
