package com.kg.component.pay.alipay.controller;

import com.ijpay.alipay.AliPayApi;
import com.kg.component.pay.alipay.dto.AliTradePayDTO;
import com.kg.component.pay.alipay.dto.AliTradeResutDTO;
import com.kg.component.pay.alipay.service.AliPayService;
import com.kg.core.annotation.IsResponseResult;
import com.kg.core.exception.BaseException;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 支付宝支付接口
 *
 * @author ziro
 * @date 2023/5/24 14:49
 */
@Api(tags = "AlipayController", value = "支付宝支付接口", description = "支付宝支付接口")
@Controller
@RequestMapping("/pay/alipay/")
public class AliPayController {

    @Resource
    private AliPayService aliPayService;

    /**
     * 扫码支付（当面付）
     *
     * @See <a href="https://opendocs.alipay.com/open/f540afd8_alipay.trade.precreate?scene=19&pathHash=d3c84596">支付宝扫码支付</a>
     */
    @PostMapping("/scanPay")
    @ResponseBody
    public AliTradeResutDTO scanPay(@RequestBody AliTradePayDTO tradePayDTO) throws BaseException {
        try {
            if (!StringUtils.hasText(tradePayDTO.getProductId()) || tradePayDTO.getTotalFee() == null) {
                throw new BaseException("您传的参数不正确");
            }
            // 获取支付二维码信息
            return aliPayService.scanPay(tradePayDTO);
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException("调起支付宝支付二维码失败！原因：" + e.getMessage());
        }
    }

    /**
     * PC网站支付（跳转到支付宝界面）
     *
     * @See <a href="https://opendocs.alipay.com/open/028r8t">支付宝PC网站支付</a>
     * TODO: 由于没有该权限，本功能未经过测试
     */
    @GetMapping("/toPcPay")
    public void getPayPc(HttpServletResponse response, AliTradePayDTO tradePayDTO)
            throws BaseException {
        try {
            if (!StringUtils.hasText(tradePayDTO.getProductId()) || tradePayDTO.getTotalFee() == null) {
                throw new BaseException("您传的参数不正确");
            }
            // 调取支付
            aliPayService.toPcPay(response, tradePayDTO);
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException("调起支付宝支付失败！原因：" + e.getMessage());
        }
    }

    /**
     * 移动网站支付（跳转到支付宝页面）
     *
     * @See <a href="https://opendocs.alipay.com/open/02ivbs">支付宝移动网站支付</a>
     * TODO: 由于没有该权限，本功能未经过测试
     */
    @GetMapping("/toWapPay")
    public void getPayWap(HttpServletResponse response, AliTradePayDTO tradePayDTO)
            throws BaseException {
        try {
            if (!StringUtils.hasText(tradePayDTO.getProductId()) || tradePayDTO.getTotalFee() == null) {
                throw new BaseException("您传的参数不正确");
            }
            // 调取支付
            aliPayService.toWapPay(response, tradePayDTO);
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException("调起支付宝支付失败！原因：" + e.getMessage());
        }
    }

    /**
     * 支付通知
     */
    @RequestMapping("payNotify")
    @ResponseBody
    @IsResponseResult(value = false)
    public String payNotify(HttpServletRequest request) {
        try {
            return aliPayService.payNotify(AliPayApi.toMap(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * return_url：支付宝支付完成，跳回的URL
     * <p>
     * 注意：
     * 由于本项目前后端分离，验证支付成功，并处理完数据后，应跳到前端地址，通知用户
     * TODO: 由于没有该权限，本功能未经过测试
     */
    @RequestMapping("returnUrl")
    public void returnUrl(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Map<String, String> params = AliPayApi.toMap(request);
            // 处理支付结果，是否支付成功
            String isSuccess = aliPayService.payNotify(params);
            if (StringUtils.hasText(isSuccess) && isSuccess.equals("success")) {
                // 支付成功
                // 跳转前端，使用绝对路径，这里要求，前后端部署到同一域下
                // 比如：前端根路径 www.xxx.com/  后端根路径 www.xxx.com/api/
                response.sendRedirect("/#/demo/trade/AlipayReturn?isSuccess=success");
                // 如果非同域部署，则需要写绝对地址
                // response.sendRedirect("http://www.xxx.com/#/demo/trade/AlipayReturn?isSuccess=success");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 跳转前端，要求同上
        response.sendRedirect("/#/demo/trade/AlipayReturn?isSuccess=fail");
    }

    /**
     * 查询支付结果
     */
    @RequestMapping("getPayResult")
    @ResponseBody
    public AliTradeResutDTO getPayResult(AliTradeResutDTO tradeResutDTO)
            throws BaseException {
        if (!StringUtils.hasText(tradeResutDTO.getTradeId())) {
            throw new BaseException("订单号不能为空");
        }
        return aliPayService.getPayResult(tradeResutDTO);
    }

}
