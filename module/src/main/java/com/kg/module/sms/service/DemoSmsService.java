package com.kg.module.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.exception.BaseException;
import com.kg.module.sms.dto.DemoSmsDTO;
import com.kg.module.sms.entity.DemoSms;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 短信 - demo 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-12-14
 */
public interface DemoSmsService extends IService<DemoSms> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return
     */
    Page<DemoSmsDTO> pagelist(Integer page, Integer limit, String params) throws BaseException;

    /**
     * 新增
     *
     * @param demoSmsDTO 新增实体
     */
    void add(DemoSmsDTO demoSmsDTO) throws BaseException;

    /**
     * 修改
     *
     * @param demoSmsDTO 编辑实体
     */
    void update(DemoSmsDTO demoSmsDTO);

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
