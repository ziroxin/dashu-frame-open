package com.kg.module.atest.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.atest.dto.ATestDTO;
import com.kg.module.atest.entity.ATest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 测试表 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-03-30
 */
public interface ATestService extends IService<ATest> {

    /**
     *  分页列表
     *
     * @param page 页码
     * @param limit 条数
     * @param params 查询条件
     * @return
     */
    Page<ATestDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param aTestDTO 新增实体
     */
    void add(ATestDTO aTestDTO);

    /**
     * 修改
     *
     * @param aTestDTO 编辑实体
     */
    void update(ATestDTO aTestDTO);

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
