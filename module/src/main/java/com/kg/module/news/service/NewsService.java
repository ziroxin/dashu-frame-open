package com.kg.module.news.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.news.entity.News;

/**
 * <p>
 * 新闻表-测试 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-02-17
 */
public interface NewsService extends IService<News> {

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    String exportExcel(String params);
}
