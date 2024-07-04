package com.kg.module.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.message.dto.MessageCountsDTO;
import com.kg.module.message.dto.ZMessageDTO;
import com.kg.module.message.entity.ZMessage;
import com.kg.module.message.dto.MessageToBaseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 消息中心 服务类
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
public interface ZMessageService extends IService<ZMessage> {

    /**
     * 发送消息
     */
    void send(MessageToBaseDTO message);

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<ZMessageDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param zMessageDTO 新增实体
     */
    void add(ZMessageDTO zMessageDTO);

    /**
     * 修改
     *
     * @param zMessageDTO 编辑实体
     */
    void update(ZMessageDTO zMessageDTO);

    /**
     * 删除关联用户，不删除消息主体
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
     * 获取消息数量
     */
    MessageCountsDTO counts();
}
