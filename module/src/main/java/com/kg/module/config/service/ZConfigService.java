package com.kg.module.config.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.config.dto.ZConfigDTO;
import com.kg.module.config.entity.ZConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 参数参数配置 服务类
 * </p>
 *
 * @author ziro
 * @since 2024-05-27
 */
public interface ZConfigService extends IService<ZConfig> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZConfigDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zConfigDTO 新增实体
     */
    void add(ZConfigDTO zConfigDTO);

    /**
     * 修改
     *
     * @param zConfigDTO 编辑实体
     */
    void update(ZConfigDTO zConfigDTO);

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    void delete(List<String> idlist);

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    String exportExcel(String params);

    /**
     * 导入Excel
     *
     * @param request 请求文件
     */
    void importExcel(HttpServletRequest request);
}
