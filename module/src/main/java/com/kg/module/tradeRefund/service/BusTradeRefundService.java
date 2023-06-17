package com.kg.module.tradeRefund.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.module.tradeRefund.dto.BusTradeRefundDTO;
import com.kg.module.tradeRefund.entity.BusTradeRefund;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 退款 - 支付demo 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-06-14
 */
public interface BusTradeRefundService extends IService<BusTradeRefund> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<BusTradeRefundDTO> pagelist(Integer page, Integer limit, String params);

    /**
     * 新增
     *
     * @param busTradeRefundDTO 新增实体
     */
    void add(BusTradeRefundDTO busTradeRefundDTO);

    /**
     * 修改
     *
     * @param busTradeRefundDTO 编辑实体
     */
    void update(BusTradeRefundDTO busTradeRefundDTO);

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
