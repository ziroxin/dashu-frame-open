package com.kg.module.aTableFiles.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.aTableFiles.dto.ATableFilesDTO;
import com.kg.module.aTableFiles.entity.ATableFiles;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 我的表a_table附件表 服务类
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
public interface ATableFilesService extends IService<ATableFiles> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @param sorts  排序条件
     * @return 分页列表
     */
    Page<ATableFilesDTO> pagelist(Integer page, Integer limit, String params, String sorts);

    /**
     * 新增
     *
     * @param aTableFilesDTO 新增实体
     */
    void add(ATableFilesDTO aTableFilesDTO);

    /**
     * 修改
     *
     * @param aTableFilesDTO 编辑实体
     */
    void update(ATableFilesDTO aTableFilesDTO);

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
     * @param sorts  排序条件
     * @return 导出后的文件url
     */
    String exportExcel(String params, String sorts);

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
