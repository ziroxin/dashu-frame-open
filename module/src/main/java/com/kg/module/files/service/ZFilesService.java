package com.kg.module.files.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.files.dto.ZFilesDTO;
import com.kg.module.files.entity.ZFiles;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 文件记录表 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-09-15
 */
public interface ZFilesService extends IService<ZFiles> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZFilesDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zFilesDTO 新增实体
     */
    void add(ZFilesDTO zFilesDTO);

    /**
     * 修改
     *
     * @param zFilesDTO 编辑实体
     */
    void update(ZFilesDTO zFilesDTO);

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
