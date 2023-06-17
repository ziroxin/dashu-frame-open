package com.kg.module.tradeRefund.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.module.tradeRefund.dto.BusTradeRefundDTO;
import com.kg.module.tradeRefund.dto.convert.BusTradeRefundConvert;
import com.kg.module.tradeRefund.entity.BusTradeRefund;
import com.kg.module.tradeRefund.excels.BusTradeRefundExcelConstant;
import com.kg.module.tradeRefund.excels.BusTradeRefundExcelOutDTO;
import com.kg.module.tradeRefund.mapper.BusTradeRefundMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 退款 - 支付demo 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-06-14
 */
@Service
public class BusTradeRefundServiceImpl extends ServiceImpl<BusTradeRefundMapper, BusTradeRefund> implements BusTradeRefundService {

    @Resource
    private BusTradeRefundConvert busTradeRefundConvert;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<BusTradeRefundDTO> pagelist(Integer page, Integer limit, String params) {
        Page<BusTradeRefund> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<BusTradeRefund> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("refundId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundId")), BusTradeRefund::getRefundId, paramObj.getStr("refundId"));
            }
            if (paramObj.containsKey("tradeId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeId")), BusTradeRefund::getTradeId, paramObj.getStr("tradeId"));
            }
            if (paramObj.containsKey("outRefundNo")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("outRefundNo")), BusTradeRefund::getOutRefundNo, paramObj.getStr("outRefundNo"));
            }
            if (paramObj.containsKey("refundDesc")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundDesc")), BusTradeRefund::getRefundDesc, paramObj.getStr("refundDesc"));
            }
            if (paramObj.containsKey("refundStatus")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundStatus")), BusTradeRefund::getRefundStatus, paramObj.getStr("refundStatus"));
            }
            if (paramObj.containsKey("refundSuccessTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundSuccessTime")), BusTradeRefund::getRefundSuccessTime, paramObj.getStr("refundSuccessTime"));
            }
            if (paramObj.containsKey("refundFee")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundFee")), BusTradeRefund::getRefundFee, paramObj.getStr("refundFee"));
            }
            if (paramObj.containsKey("refundResultJson")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundResultJson")), BusTradeRefund::getRefundResultJson, paramObj.getStr("refundResultJson"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), BusTradeRefund::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), BusTradeRefund::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        //返回数据
        Page<BusTradeRefund> pageEntity = page(pager, wrapper);
        Page<BusTradeRefundDTO> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> busTradeRefundConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param busTradeRefundDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(BusTradeRefundDTO busTradeRefundDTO) {
        BusTradeRefund busTradeRefund = busTradeRefundConvert.dtoToEntity(busTradeRefundDTO);
        busTradeRefund.setRefundId(GuidUtils.getUuid());
        busTradeRefund.setCreateTime(LocalDateTime.now());
        save(busTradeRefund);
    }

    /**
     * 修改
     *
     * @param busTradeRefundDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(BusTradeRefundDTO busTradeRefundDTO) {
        BusTradeRefund busTradeRefund = busTradeRefundConvert.dtoToEntity(busTradeRefundDTO);
        busTradeRefund.setUpdateTime(LocalDateTime.now());
        updateById(busTradeRefund);
    }

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(List<String> idlist) {
        removeBatchByIds(idlist);
    }

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    @Override
    public String exportExcel(String params) {
        try {
            // 拼接导出Excel的文件，保存的临时路径
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/"
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<BusTradeRefund> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("refundId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundId")), BusTradeRefund::getRefundId, paramObj.getStr("refundId"));
                }
                if (paramObj.containsKey("tradeId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeId")), BusTradeRefund::getTradeId, paramObj.getStr("tradeId"));
                }
                if (paramObj.containsKey("outRefundNo")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("outRefundNo")), BusTradeRefund::getOutRefundNo, paramObj.getStr("outRefundNo"));
                }
                if (paramObj.containsKey("refundDesc")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundDesc")), BusTradeRefund::getRefundDesc, paramObj.getStr("refundDesc"));
                }
                if (paramObj.containsKey("refundStatus")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundStatus")), BusTradeRefund::getRefundStatus, paramObj.getStr("refundStatus"));
                }
                if (paramObj.containsKey("refundSuccessTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundSuccessTime")), BusTradeRefund::getRefundSuccessTime, paramObj.getStr("refundSuccessTime"));
                }
                if (paramObj.containsKey("refundFee")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundFee")), BusTradeRefund::getRefundFee, paramObj.getStr("refundFee"));
                }
                if (paramObj.containsKey("refundResultJson")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundResultJson")), BusTradeRefund::getRefundResultJson, paramObj.getStr("refundResultJson"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), BusTradeRefund::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), BusTradeRefund::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<BusTradeRefund> list = list(wrapper);
            // 转换成导出excel实体
            List<BusTradeRefundExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), BusTradeRefundExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new BusTradeRefundExcelOutDTO());
            }
            // 第一行标题
            String title = "退款 - 支付demo";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, BusTradeRefundExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 导入Excel
     *
     * @param request 请求文件
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void importExcel(HttpServletRequest request) {
        // 读取导入数据
        List<BusTradeRefund> importData =
                ExcelCommonUtils.read(request, 1, 2, BusTradeRefund.class, BusTradeRefundExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<BusTradeRefund> saveData = importData.stream().map(o -> {
            o.setRefundId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
