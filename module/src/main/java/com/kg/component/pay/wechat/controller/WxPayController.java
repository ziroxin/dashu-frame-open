package com.kg.component.pay.wechat.controller;

import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.HttpKit;
import com.kg.component.pay.wechat.dto.WxTradePayDTO;
import com.kg.component.pay.wechat.dto.WxTradeRefundDTO;
import com.kg.component.pay.wechat.dto.WxTradeResutDTO;
import com.kg.component.pay.wechat.service.WxPayService;
import com.kg.component.utils.IpUtils;
import com.kg.core.exception.BaseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 微信支付接口 - 商户号
 *
 * @author ziro
 * @date 2023-04-09 22:21:42
 */
@Api(tags = "WxPayController", value = "微信支付接口", description = "微信支付接口")
@RestController
@RequestMapping("/pay/wechat/")
public class WxPayController {

    @Resource
    private WxPayService wxPayService;

    /**
     * 支付方式1：
     * NATIVE - 扫码支付 - 固定金额扫码
     *
     * @See <a href="https://pay.weixin.qq.com/doc/v2/merchant/4011936204">V2-NATIVE扫码支付</a>
     * @See <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012791874">V3-NATIVE扫码支付</a>
     */
    @ApiOperation(value = "/pay/wechat/getPayNative", notes = "Native-PC端扫码支付", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求信息", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "tradePayDTO", value = "交易信息", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("/getPayNative")
    public WxTradeResutDTO getPayNative(HttpServletRequest request, @RequestBody WxTradePayDTO wxTradePayDTO)
            throws BaseException {
        try {
            if (!StringUtils.hasText(wxTradePayDTO.getProductId()) || wxTradePayDTO.getTotalFee() == null) {
                throw new BaseException("您传的参数不正确");
            }
            wxTradePayDTO.setSpbillCreateIp(IpUtils.getClientIP(request));
            // 调取支付
            return wxPayService.getPayInfo(wxTradePayDTO, TradeType.NATIVE.getTradeType());
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException("调起微信支付二维码失败！原因：" + e.getMessage());
        }
    }

    /**
     * 支付方式2：
     * H5 - 移动端浏览器(非微信客户端)，调起微信H5支付
     *
     * @See <a href="https://pay.weixin.qq.com/doc/v2/merchant/4011936237">V2-H5支付</a>
     * @See <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012791832">V3-H5支付</a>
     */
    @ApiOperation(value = "/pay/wechat/getPayH5", notes = "H5-移动端网页支付", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求信息", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "tradePayDTO", value = "交易信息", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("/getPayH5")
    public WxTradeResutDTO getPayH5(HttpServletRequest request, @RequestBody WxTradePayDTO wxTradePayDTO)
            throws BaseException {
        try {
            if (!StringUtils.hasText(wxTradePayDTO.getProductId()) || wxTradePayDTO.getTotalFee() == null) {
                throw new BaseException("您传的参数不正确");
            }
            wxTradePayDTO.setSpbillCreateIp(IpUtils.getClientIP(request));
            // 调取支付
            return wxPayService.getPayInfo(wxTradePayDTO, TradeType.MWEB.getTradeType());
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException("调起微信支付失败！原因：" + e.getMessage());
        }
    }

    /**
     * 支付方式3：
     * JSAPI - 微信客户端内，调起微信支付
     * 注意：
     * 移动端微信支付，采用jsapi方式
     * 1. 只能在微信客户端内完成；
     * 2. 且用户必须先（商户号关联的公众号）授权，需要先获取用户的openid
     *
     * @See <a href="https://pay.weixin.qq.com/doc/v2/merchant/4011935206">V2-JSAPI微信客户端支付</a>
     * @See <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012062524">V3-JSAPI微信客户端支付</a>
     */
    @ApiOperation(value = "/pay/wechat/getPayJsapi", notes = "JSAPI-微信客户端支付", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求信息", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "tradePayDTO", value = "交易信息", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("/getPayJsapi")
    public WxTradeResutDTO getPayJsapi(HttpServletRequest request, @RequestBody WxTradePayDTO wxTradePayDTO)
            throws BaseException {
        try {

            if (!StringUtils.hasText(wxTradePayDTO.getProductId()) || wxTradePayDTO.getTotalFee() == null
                    // openId必填
                    || wxTradePayDTO.getOpenId() == null) {
                throw new BaseException("您传的参数不正确");
            }
            wxTradePayDTO.setSpbillCreateIp(IpUtils.getClientIP(request));
            // 调取支付
            return wxPayService.getPayInfo(wxTradePayDTO, TradeType.JSAPI.getTradeType());
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException("调起微信支付失败！原因：" + e.getMessage());
        }
    }

    /**
     * 支付回调
     * 只有支付成功时，会回调
     *
     * @See <a href="https://pay.weixin.qq.com/doc/v2/merchant/4011937152">V2-支付结果通知</a>
     * @See <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012791882">V3-支付结果通知</a>
     */
    @PostMapping("payNotify")
    public String payNotify(HttpServletRequest request) {
        try {
            return wxPayService.payNotify(HttpKit.readData(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询支付结果
     *
     * @See <a href="https://pay.weixin.qq.com/doc/v2/merchant/4011935215">V2-查询订单</a>
     * @See <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012791859">V3-查询订单</a>
     */
    @ApiOperation(value = "/pay/wechat/getPayResult", notes = "查询支付结果", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tradeResutDTO", value = "支付订单信息", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/getPayResult")
    public WxTradeResutDTO getPayResult(WxTradeResutDTO wxTradeResutDTO) throws BaseException {
        if (!StringUtils.hasText(wxTradeResutDTO.getTradeId())) {
            throw new BaseException("订单号不能为空");
        }
        return wxPayService.getPayResult(wxTradeResutDTO);
    }

    /**
     * 微信退款
     *
     * <a href="https://pay.weixin.qq.com/doc/v2/merchant/4011935217">V2-退款文档</a>
     * <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012791862">V3-退款文档</a>
     */
    @PostMapping("/refund")
    public WxTradeRefundDTO refund(@RequestBody WxTradeRefundDTO wxTradeRefundDTO) throws BaseException {
        try {
            if (!StringUtils.hasText(wxTradeRefundDTO.getTradeId())) {
                throw new BaseException("您传的参数不正确");
            }
            // 微信退款
            return wxPayService.refund(wxTradeRefundDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("退款失败！原因：" + e.getMessage());
        }
    }

    /**
     * 查询退款结果
     * <a href="https://pay.weixin.qq.com/doc/v2/merchant/4011935218">V2-查询退款</a>
     * <a href="https://pay.weixin.qq.com/doc/v3/merchant/4012791863">V3-查询单笔退款</a>
     */
    @GetMapping("/queryRefund")
    public WxTradeRefundDTO queryRefund(WxTradeRefundDTO wxTradeRefundDTO) throws BaseException {
        return wxPayService.queryRefund(wxTradeRefundDTO);
    }

}
