package com.kg.core.zquartz.service;

import com.kg.core.zquartz.dto.ZQuartzDTO;
import com.kg.core.zquartz.entity.ZQuartz;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 定时任务调度表 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-12-26
 */
public interface ZQuartzService extends IService<ZQuartz> {

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    String exportExcel(String params);

    /**
     * 任务名称是否已存在
     */
    boolean isJobExit(ZQuartzDTO zQuartzDTO);
}
