package com.kg.module.trade.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.module.trade.dto.BusTradeDTO;
import com.kg.module.trade.dto.convert.BusTradeConvert;
import com.kg.module.trade.entity.BusTrade;
import com.kg.module.trade.excels.BusTradeExcelConstant;
import com.kg.module.trade.excels.BusTradeExcelOutDTO;
import com.kg.module.trade.mapper.BusTradeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 交易 - 支付demo 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-05-16
 */
@Service
public class BusTradeServiceImpl extends ServiceImpl<BusTradeMapper, BusTrade> implements BusTradeService {

    @Resource
    private BusTradeConvert busTradeConvert;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<BusTradeDTO> pagelist(Integer page, Integer limit, String params) {
        Page<BusTrade> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<BusTrade> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("tradeId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeId")), BusTrade::getTradeId, paramObj.getStr("tradeId"));
            }
            if (paramObj.containsKey("productId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("productId")), BusTrade::getProductId, paramObj.getStr("productId"));
            }
            if (paramObj.containsKey("userId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("userId")), BusTrade::getUserId, paramObj.getStr("userId"));
            }
            if (paramObj.containsKey("payType")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("payType")), BusTrade::getPayType, paramObj.getStr("payType"));
            }
            if (paramObj.containsKey("tradeOpenId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeOpenId")), BusTrade::getTradeOpenId, paramObj.getStr("tradeOpenId"));
            }
            if (paramObj.containsKey("tradeStatus")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeStatus")), BusTrade::getTradeStatus, paramObj.getStr("tradeStatus"));
            }
            if (paramObj.containsKey("paySuccessTime")) {
                if (StringUtils.hasText(paramObj.getStr("paySuccessTime"))) {
                    System.out.println(paramObj.getStr("paySuccessTime"));
                    String paySuccessTime =
                            LocalDateTime.parse(paramObj.getStr("paySuccessTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
                                    .plusHours(8)// +8小时
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    System.out.println(paySuccessTime);
                    wrapper.lambda().likeRight(BusTrade::getPaySuccessTime, paySuccessTime);
                }
            }
            if (paramObj.containsKey("totalFee")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("totalFee")), BusTrade::getTotalFee, paramObj.getStr("totalFee"));
            }
            if (paramObj.containsKey("outTradeNo")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("outTradeNo")), BusTrade::getOutTradeNo, paramObj.getStr("outTradeNo"));
            }
            if (paramObj.containsKey("resultJson")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("resultJson")), BusTrade::getResultJson, paramObj.getStr("resultJson"));
            }
            if (paramObj.containsKey("refundTotalFee")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundTotalFee")), BusTrade::getRefundTotalFee, paramObj.getStr("refundTotalFee"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), BusTrade::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), BusTrade::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        wrapper.lambda().orderByDesc(BusTrade::getPaySuccessTime).orderByDesc(BusTrade::getCreateTime);
        //返回数据
        Page<BusTrade> pageEntity = page(pager, wrapper);
        Page<BusTradeDTO> result = new Page<>();
        result.setRecords(pageEntity.getRecords().stream().map(m -> busTradeConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param busTradeDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(BusTradeDTO busTradeDTO) {
        BusTrade busTrade = busTradeConvert.dtoToEntity(busTradeDTO);
        busTrade.setTradeId(GuidUtils.getUuid());
        busTrade.setCreateTime(LocalDateTime.now());
        save(busTrade);
    }

    /**
     * 修改
     *
     * @param busTradeDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(BusTradeDTO busTradeDTO) {
        BusTrade busTrade = busTradeConvert.dtoToEntity(busTradeDTO);
        busTrade.setUpdateTime(LocalDateTime.now());
        updateById(busTrade);
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
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<BusTrade> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("tradeId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeId")), BusTrade::getTradeId, paramObj.getStr("tradeId"));
                }
                if (paramObj.containsKey("productId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("productId")), BusTrade::getProductId, paramObj.getStr("productId"));
                }
                if (paramObj.containsKey("userId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("userId")), BusTrade::getUserId, paramObj.getStr("userId"));
                }
                if (paramObj.containsKey("payType")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("payType")), BusTrade::getPayType, paramObj.getStr("payType"));
                }
                if (paramObj.containsKey("tradeOpenId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeOpenId")), BusTrade::getTradeOpenId, paramObj.getStr("tradeOpenId"));
                }
                if (paramObj.containsKey("tradeStatus")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tradeStatus")), BusTrade::getTradeStatus, paramObj.getStr("tradeStatus"));
                }
                if (paramObj.containsKey("paySuccessTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("paySuccessTime")), BusTrade::getPaySuccessTime, paramObj.getStr("paySuccessTime"));
                }
                if (paramObj.containsKey("totalFee")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("totalFee")), BusTrade::getTotalFee, paramObj.getStr("totalFee"));
                }
                if (paramObj.containsKey("outTradeNo")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("outTradeNo")), BusTrade::getOutTradeNo, paramObj.getStr("outTradeNo"));
                }
                if (paramObj.containsKey("resultJson")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("resultJson")), BusTrade::getResultJson, paramObj.getStr("resultJson"));
                }
                if (paramObj.containsKey("refundTotalFee")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refundTotalFee")), BusTrade::getRefundTotalFee, paramObj.getStr("refundTotalFee"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), BusTrade::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), BusTrade::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<BusTrade> list = list(wrapper);
            // 转换成导出excel实体
            List<BusTradeExcelOutDTO> dataList = list.stream().map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), BusTradeExcelOutDTO.class)).collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new BusTradeExcelOutDTO());
            }
            // 第一行标题
            String title = "交易 - 支付demo";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, BusTradeExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<BusTrade> importData = ExcelCommonUtils.read(request, 1, 2, BusTrade.class, BusTradeExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<BusTrade> saveData = importData.stream().map(o -> {
            o.setTradeId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
