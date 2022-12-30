package com.kg.core.zsafety.service;

import com.kg.core.zsafety.entity.ZSafety;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 密码安全等设置 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-12-30
 */
public interface ZSafetyService extends IService<ZSafety> {

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    String exportExcel(String params);
}
