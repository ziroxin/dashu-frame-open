package com.kg.core.zorg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.zorg.entity.ZOrganization;

/**
 * <p>
 * 组织机构表 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-01-11
 */
public interface ZOrganizationService extends IService<ZOrganization> {

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    String exportExcel(String params);
}
