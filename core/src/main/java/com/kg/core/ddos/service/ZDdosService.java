package com.kg.core.ddos.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.ddos.dto.ZDdosDTO;
import com.kg.core.ddos.entity.ZDdos;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * ddos用户请求记录 服务类
 * </p>
 *
 * @author ziro
 * @since 2024-07-15
 */
public interface ZDdosService extends IService<ZDdos> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZDdosDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zDdosDTO 新增实体
     */
    void add(ZDdosDTO zDdosDTO);

    /**
     * 修改
     *
     * @param zDdosDTO 编辑实体
     */
    void update(ZDdosDTO zDdosDTO);

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
