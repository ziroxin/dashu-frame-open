package com.kg.module.atable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.atable.dto.ATableDTO;
import com.kg.module.atable.entity.ATable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 我的表a_table 服务类
 * </p>
 *
 * @author ziro
 * @since 2025-02-11
 */
public interface ATableService extends IService<ATable> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @param sorts  排序条件
     * @return 分页列表
     */
    Page<ATableDTO> pagelist(Integer page, Integer limit, String params, String sorts);

    /**
     * 新增
     *
     * @param aTableDTO 新增实体
     */
    void add(ATableDTO aTableDTO);

    /**
     * 修改
     *
     * @param aTableDTO 编辑实体
     */
    void update(ATableDTO aTableDTO);

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
    String importExcel(HttpServletRequest request);

    /**
     * 下载导入模板
     *
     * @return 模板文件url
     */
    String downloadTemplate();
}
