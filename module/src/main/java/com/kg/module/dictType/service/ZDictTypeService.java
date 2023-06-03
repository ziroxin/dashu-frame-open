package com.kg.module.dictType.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.exception.BaseException;
import com.kg.module.dictType.dto.ZDictTypeDTO;
import com.kg.module.dictType.entity.ZDictType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 字典类型 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
public interface ZDictTypeService extends IService<ZDictType> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZDictTypeDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zDictTypeDTO 新增实体
     */
    void add(ZDictTypeDTO zDictTypeDTO) throws BaseException;

    /**
     * 修改
     *
     * @param zDictTypeDTO 编辑实体
     */
    void update(ZDictTypeDTO zDictTypeDTO) throws BaseException;

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    void delete(List<String> idlist) throws BaseException;

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
