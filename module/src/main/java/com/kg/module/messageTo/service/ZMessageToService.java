package com.kg.module.messageTo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.messageTo.dto.ZMessageToDTO;
import com.kg.module.messageTo.entity.ZMessageTo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 消息发送至 服务类
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
public interface ZMessageToService extends IService<ZMessageTo> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZMessageToDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zMessageToDTO 新增实体
     */
    void add(ZMessageToDTO zMessageToDTO);

    /**
     * 修改
     *
     * @param zMessageToDTO 编辑实体
     */
    void update(ZMessageToDTO zMessageToDTO);

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
