package com.kg.module.dictData.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.dictData.dto.ZDictDataDTO;
import com.kg.module.dictData.entity.ZDictData;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 字典数据 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
public interface ZDictDataService extends IService<ZDictData> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZDictDataDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zDictDataDTO 新增实体
     */
    void add(ZDictDataDTO zDictDataDTO);

    /**
     * 修改
     *
     * @param zDictDataDTO 编辑实体
     */
    void update(ZDictDataDTO zDictDataDTO);

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

    /**
     * 获取全部字典（缓存redis）
     */
    List<ZDictData> listCache(String typeCode);

    /**
     * 清空数据字典缓存数据
     */
    void clearCache(String typeCode);
}
