package com.kg.module.applet.wechat2user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.applet.wechat2user.dto.ZUserWechatDTO;
import com.kg.module.applet.wechat2user.entity.ZUserWechat;

import java.util.List;

/**
 * <p>
 * 用户-微信-绑定关系表 服务类
 * </p>
 *
 * @author ziro
 * @since 2024-12-17
 */
public interface ZUserWechatService extends IService<ZUserWechat> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZUserWechatDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zUserWechatDTO 新增实体
     */
    void add(ZUserWechatDTO zUserWechatDTO);

    /**
     * 修改
     *
     * @param zUserWechatDTO 编辑实体
     */
    void update(ZUserWechatDTO zUserWechatDTO);

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    void delete(List<String> idlist);

}
