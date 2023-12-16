package com.kg.component.sms.aliyun;


import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


/**
 * 阿里云短信发送工具类
 *
 * @author ziro
 * @date 2023/12/13 14:37
 */
@Component
public class AliyunSmsSender {
    @Resource
    private AliyunConfig aliyunConfig;

    /**
     * 发送短信
     *
     * @param templateId 短信模板id（例如：SMS_1875001）
     * @param phones     手机号（支持对多个手机号码发送短信，手机号码之间以半角逗号,分隔。上限为1000个手机号码）
     * @param paramJson  短信模板参数（例如：{"code":"123456"}）
     */
    public AliyunSmsResult send(String templateId, String phones, String paramJson) {
        return sendCoverConfig(null, templateId, phones, paramJson);
    }

    /**
     * 发送短信 - 自定义签名
     *
     * @param sign       签名
     * @param templateId 短信模板id（例如：SMS_1875001）
     * @param phones     手机号（支持对多个手机号码发送短信，手机号码之间以半角逗号,分隔。上限为1000个手机号码）
     * @param paramJson  短信模板参数（例如：{"code":"123456"})
     */
    public AliyunSmsResult sendCoverSign(String sign, String templateId, String phones, String paramJson) {
        AliyunConfig cfg = aliyunConfig;
        cfg.setSign(sign);// 覆盖签名
        return sendCoverConfig(cfg, templateId, phones, paramJson);
    }

    /**
     * 发送短信 - 自定义配置
     *
     * @param cfg        阿里云短信配置
     * @param templateId 短信模板id（例如：SMS_1875001）
     * @param phones     手机号（支持对多个手机号码发送短信，手机号码之间以半角逗号,分隔。上限为1000个手机号码）
     * @param paramJson  短信模板参数（例如：{"code":"123456"})
     */
    public AliyunSmsResult sendCoverConfig(AliyunConfig cfg, String templateId, String phones, String paramJson) {
        AliyunSmsResult result = new AliyunSmsResult();
        try {
            System.out.println("=====");
            System.out.println(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"));
            System.out.println("=====");
            // 创建阿里云Client
            Client client = createClient(cfg != null ? cfg : aliyunConfig);
            // 短信签名
            String sign = cfg != null && StringUtils.hasText(cfg.getSign()) ? cfg.getSign() : aliyunConfig.getSign();
            result.setSign(sign);
            // 创建短信request
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setSignName(sign)
                    .setTemplateCode(templateId)
                    .setPhoneNumbers(phones)
                    .setTemplateParam(paramJson);
            // 开始发送短信
            SendSmsResponseBody body = client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions()).getBody();
            if (body != null) {
                if (StringUtils.hasText(body.getCode()) && "OK".equals(body.getCode())) {
                    // 发送成功
                    result.setSuccess(true);
                    result.setResultJson(JSONUtil.toJsonStr(body));
                    return result;
                } else {
                    // 发送失败
                    StaticLog.warn("短信发送失败！失败信息body：" + body);
                    result.setSuccess(false);
                    result.setErrorMessage(body.getMessage());
                    result.setResultJson(JSONUtil.toJsonStr(body));
                    return result;
                }
            }
        } catch (TeaException error) {
            System.out.println(error.getMessage() + "\nRecommend:" + error.getData().get("Recommend"));
            result.setSuccess(false);
            result.setErrorMessage(error.getMessage());
            return result;
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            System.out.println(error.getMessage() + "\nRecommend:" + error.getData().get("Recommend"));
            result.setSuccess(false);
            result.setErrorMessage(error.getMessage());
            return result;
        }
        result.setErrorMessage("短信发送失败！未知异常");
        return result;
    }

    /**
     * 创建阿里云短信Client
     *
     * @param cfg 选填：阿里云配置信息（可为空，默认使用application.yml中的配置）
     * @return Client 发短信client
     * @throws Exception
     */
    private Client createClient(AliyunConfig cfg) throws Exception {
        Config config = new Config();
        if (cfg != null) {
            config.setAccessKeyId(StringUtils.hasText(cfg.getAccessKeyId()) ? cfg.getAccessKeyId() : aliyunConfig.getAccessKeyId());
            config.setAccessKeySecret(StringUtils.hasText(cfg.getAccessKeySecret()) ? cfg.getAccessKeySecret() : aliyunConfig.getAccessKeySecret());
            config.setEndpoint(StringUtils.hasText(cfg.getEndpoint()) ? cfg.getEndpoint() : aliyunConfig.getEndpoint());
        } else {
            config.setAccessKeyId(aliyunConfig.getAccessKeyId());
            config.setAccessKeySecret(aliyunConfig.getAccessKeySecret());
            config.setEndpoint(aliyunConfig.getEndpoint());
        }
        return new Client(config);
    }
}
