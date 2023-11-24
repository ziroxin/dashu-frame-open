package com.kg.module.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.generator.dto.ZFormGeneratorDTO;
import com.kg.module.generator.entity.ZFormGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 代码生成器表单 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-11-22
 */
public interface ZFormGeneratorService extends IService<ZFormGenerator> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZFormGeneratorDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zFormGeneratorDTO 新增实体
     */
    String add(ZFormGeneratorDTO zFormGeneratorDTO);

    /**
     * 修改
     *
     * @param zFormGeneratorDTO 编辑实体
     */
    void update(ZFormGeneratorDTO zFormGeneratorDTO);

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
