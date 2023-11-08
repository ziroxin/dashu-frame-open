package com.kg.module.userTheme.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.userTheme.dto.ZUserThemeDTO;
import com.kg.module.userTheme.entity.ZUserTheme;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 用户主题配置 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-11-04
 */
public interface ZUserThemeService extends IService<ZUserTheme> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZUserThemeDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zUserThemeDTO 新增实体
     */
    void add(ZUserThemeDTO zUserThemeDTO);

    /**
     * 修改
     *
     * @param zUserThemeDTO 编辑实体
     */
    void update(ZUserThemeDTO zUserThemeDTO);

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
