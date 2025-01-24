package com.kg.module.filesStatic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.filesStatic.dto.ZFilesStaticDTO;
import com.kg.module.filesStatic.entity.ZFilesStatic;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 静态资源文件表 服务类
 * </p>
 *
 * @author ziro
 * @since 2025-01-24
 */
public interface ZFilesStaticService extends IService<ZFilesStatic> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZFilesStaticDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zFilesStaticDTO 新增实体
     */
    void add(ZFilesStaticDTO zFilesStaticDTO);

    /**
     * 修改
     *
     * @param zFilesStaticDTO 编辑实体
     */
    void update(ZFilesStaticDTO zFilesStaticDTO);

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
