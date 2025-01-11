package com.kg.core.zorg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.exception.BaseException;
import com.kg.core.zorg.dto.ZOrganizationDTO;
import com.kg.core.zorg.dto.ZOrganizationTreeSelectDTO;
import com.kg.core.zorg.entity.ZOrganization;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 组织机构表 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-01-111
 */
public interface ZOrganizationService extends IService<ZOrganization> {

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    String exportExcel(String params);

    /**
     * 组织机构树
     *
     * @param orgName  根据名称查询-模糊查询
     * @param parentId 父级ID
     * @return 组织机构树
     */
    List<ZOrganizationDTO> tree(String orgName, String parentId);

    /**
     * 下拉选择框组织机构树
     */
    List<ZOrganizationTreeSelectDTO> treeForSelect();

    /**
     * 查询父级组织机构树
     */
    List<ZOrganizationDTO> parentTree();

    /**
     * 获取最大层级
     */
    Integer getMaxLevel();

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

    /**
     * 新增组织机构
     * @param zOrganizationDTO 组织机构信息
     */
    void add(ZOrganizationDTO zOrganizationDTO) throws BaseException;

    /**
     * 修改组织机构
     * @param zOrganizationDTO 组织机构信息
     */
    void update(ZOrganizationDTO zOrganizationDTO) throws BaseException;
}
