package com.kg.component.pay.wechat.service;

import cn.hutool.json.JSONUtil;
import com.ijpay.core.kit.WxPayKit;
import com.kg.component.pay.wechat.config.WxPayConfig;
import com.kg.component.pay.wechat.dto.WxTradePayDTO;
import com.kg.component.pay.wechat.dto.WxTradeRefundDTO;
import com.kg.component.pay.wechat.dto.WxTradeResutDTO;
import com.kg.component.pay.wechat.utils.WxPayV2ServiceUtils;
import com.kg.component.pay.wechat.utils.WxPayV3ServiceUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.module.trade.constant.TradeConstant;
import com.kg.module.trade.entity.BusTrade;
import com.kg.module.trade.service.BusTradeService;
import com.kg.module.tradeRefund.entity.BusTradeRefund;
import com.kg.module.tradeRefund.service.BusTradeRefundService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 微信支付服务
 *
 * @author ziro
 * @date 2023/5/22 17:07
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Resource
    private WxPayConfig wxPayConfig;
    @Resource
    private BusTradeService tradeService;
    @Resource
    private BusTradeRefundService refundService;

    @Override
    public WxTradeResutDTO getPayInfo(WxTradePayDTO wxTradePayDTO, String tradeType) throws BaseException {
        // 统一支付：组装订单信息
        BusTrade busTrade = new BusTrade();
        busTrade.setTradeId(GuidUtils.getUuid());
        busTrade.setProductId(wxTradePayDTO.getProductId());
        busTrade.setUserId(StringUtils.hasText(wxTradePayDTO.getUserId())
                ? wxTradePayDTO.getUserId() : CurrentUserUtils.getCurrentUser().getUserId());
        busTrade.setPayType(TradeConstant.PAY_TYPE_WECHAT);
        busTrade.setTradeStatus(TradeConstant.TRADE_FAIL);
        busTrade.setTotalFee(wxTradePayDTO.getTotalFee());
        busTrade.setOutTradeNo(WxPayKit.generateStr());
        busTrade.setAttach(wxTradePayDTO.getAttach());
        busTrade.setCreateTime(LocalDateTime.now());

        if (wxPayConfig.getKeyVersion().equals("v3")) {
            // V3版支付
            return WxPayV3ServiceUtils.toV3Pay(wxPayConfig, tradeService, wxTradePayDTO, tradeType, busTrade);
        } else {
            // V2版支付
            return WxPayV2ServiceUtils.toV2Pay(wxPayConfig, tradeService, wxTradePayDTO, tradeType, busTrade);
        }
    }

    @Override
    public String payNotify(String result) {
        if (wxPayConfig.getKeyVersion().equals("v3")) {
            // V3版支付通知
            return WxPayV3ServiceUtils.payNotify(result, wxPayConfig, tradeService);
        } else {
            // V2版支付通知
            return WxPayV2ServiceUtils.payNotify(result, wxPayConfig, tradeService);
        }
    }

    @Override
    public WxTradeResutDTO getPayResult(WxTradeResutDTO wxTradeResutDTO) {
        BusTrade b = tradeService.lambdaQuery()
                .eq(BusTrade::getTradeId, wxTradeResutDTO.getTradeId()).one();
        if (b != null && TradeConstant.TRADE_SUCCESS.intValue() == b.getTradeStatus().intValue()) {
            // 已经支付成功
            wxTradeResutDTO.setTradeStatus(TradeConstant.TRADE_SUCCESS);
            return wxTradeResutDTO;
        }

        if (wxPayConfig.getKeyVersion().equals("v3")) {
            // V3版支付结果查询
            return WxPayV3ServiceUtils.orderQuery(wxPayConfig, tradeService, b, wxTradeResutDTO);
        } else {
            // V2版支付结果查询
            return WxPayV2ServiceUtils.orderQuery(wxPayConfig, tradeService, b, wxTradeResutDTO);
        }
    }

    @Override
    public WxTradeRefundDTO refund(WxTradeRefundDTO wxTradeRefundDTO) throws BaseException {
        // 是否有退款中的订单
        Long count = refundService.lambdaQuery()
                .eq(BusTradeRefund::getTradeId, wxTradeRefundDTO.getTradeId())
                .eq(BusTradeRefund::getRefundStatus, 0).count();
        if (count > 0) {
            throw new BaseException("有" + count + "笔订单正在退款中，为防止重复退款，请稍后再试！");
        }
        // 校验退款金额
        BusTrade busTrade = tradeService.lambdaQuery().eq(BusTrade::getTradeId, wxTradeRefundDTO.getTradeId()).one();
        Integer refundTotalFee = busTrade.getRefundTotalFee() == null ? 0 : busTrade.getRefundTotalFee();
        if (busTrade.getTotalFee() < (refundTotalFee + wxTradeRefundDTO.getRefundFee())) {
            throw new BaseException("退款金额合计，不能大于支付总金额!");
        }
        // 生成退款信息
        BusTradeRefund refund = new BusTradeRefund();
        refund.setRefundId(GuidUtils.getUuid());
        refund.setTradeId(wxTradeRefundDTO.getTradeId());
        refund.setOutRefundNo(WxPayKit.generateStr());
        refund.setRefundFee(wxTradeRefundDTO.getRefundFee());
        refund.setRefundDesc(wxTradeRefundDTO.getRefundDesc());
        refund.setRefundIndex(refundService.lambdaQuery()
                .eq(BusTradeRefund::getTradeId, wxTradeRefundDTO.getTradeId())
                .count().intValue());

        if (wxPayConfig.getKeyVersion().equals("v3")) {
            // V3版退款
            return WxPayV3ServiceUtils.refund(wxPayConfig, refundService, busTrade, refund, wxTradeRefundDTO);
        } else {
            // V2版退款
            return WxPayV2ServiceUtils.refund(wxPayConfig, refundService, busTrade, refund, wxTradeRefundDTO);
        }
    }

    @Override
    public WxTradeRefundDTO queryRefund(WxTradeRefundDTO wxTradeRefundDTO) throws BaseException {
        // 查询退款记录
        BusTradeRefund refund = refundService.getById(wxTradeRefundDTO.getRefundId());
        // 退款记录不存在
        if (refund == null) {
            throw new BaseException("退款信息不存在，请刷新重试！");
        }
        // 退款状态已更新，直接返回
        if (refund.getRefundStatus().intValue() != 0) {
            return JSONUtil.toBean(JSONUtil.parseObj(refund, true), WxTradeRefundDTO.class);
        }
        // 去微信查询退款信息
        if (wxPayConfig.getKeyVersion().equals("v3")) {
            // V3版查询退款信息
            return WxPayV3ServiceUtils.queryRefund(wxPayConfig, refundService,
                    tradeService, refund, wxTradeRefundDTO);
        } else {
            // V2版查询退款信息
            return WxPayV2ServiceUtils.queryRefund(wxPayConfig, refundService,
                    tradeService, refund, wxTradeRefundDTO);
        }
    }
}
