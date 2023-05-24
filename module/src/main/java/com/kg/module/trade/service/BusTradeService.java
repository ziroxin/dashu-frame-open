package com.kg.module.trade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.trade.dto.BusTradeDTO;
import com.kg.module.trade.entity.BusTrade;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 交易 - 支付demo 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-05-16
 */
public interface BusTradeService extends IService<BusTrade> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<BusTradeDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param busTradeDTO 新增实体
     */
    void add(BusTradeDTO busTradeDTO);

    /**
     * 修改
     *
     * @param busTradeDTO 编辑实体
     */
    void update(BusTradeDTO busTradeDTO);

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
