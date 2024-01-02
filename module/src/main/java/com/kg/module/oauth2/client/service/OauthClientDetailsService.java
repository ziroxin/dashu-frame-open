package com.kg.module.oauth2.client.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.oauth2.client.dto.OauthClientDetailsDTO;
import com.kg.module.oauth2.client.entity.OauthClientDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * Oauth2客户端信息表 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-09-12
 */
public interface OauthClientDetailsService extends IService<OauthClientDetails> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<OauthClientDetailsDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param oauthClientDetailsDTO 新增实体
     */
    void add(OauthClientDetailsDTO oauthClientDetailsDTO);

    /**
     * 修改
     *
     * @param oauthClientDetailsDTO 编辑实体
     */
    void update(OauthClientDetailsDTO oauthClientDetailsDTO);

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
