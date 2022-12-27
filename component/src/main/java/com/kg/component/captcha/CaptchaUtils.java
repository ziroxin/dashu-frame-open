package com.kg.component.captcha;

import com.kg.component.utils.GuidUtils;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.ChineseGifCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Date;

/**
 * 生成验证码工具类
 *
 * @author ziro
 * @date 2022-12-27 10:27:20
 */
@Setter
@Getter
public class CaptchaUtils {
    private CaptchaEntity captchaEntity;

    /**
     * 获取验证码 - 生产验证码
     *
     * @return 验证码
     */
    public CaptchaResult getCaptcha() {
        CaptchaResult cr = new CaptchaResult();
        // 有效期（当前时间 + 配置有效期）
        cr.setCodeExpire(new Date(new Date().getTime() + (captchaEntity.getExpiration() * 60 * 1000)));
        // 生成验证码
        Captcha captcha = createCaptcha();
        // 验证码内容
        cr.setCodeUuid(GuidUtils.getUuid());
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == CaptchaTypeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            // 当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
            captchaValue = captchaValue.split("\\.")[0];
        }
        cr.setCodeValue(captchaValue);
        // base64图片
        cr.setCodeBaseImage(captcha.toBase64());
        return cr;
    }

    /**
     * 生产验证码
     */
    private Captcha createCaptcha() {
        Captcha captcha = null;
        synchronized (this) {
            switch (captchaEntity.getCaptchaType()) {
                case ARITHMETIC:
                    captcha = new CaptchaFixedArithmetic(captchaEntity.getWidth(), captchaEntity.getHeight());
                    captcha.setLen(captchaEntity.getLength());
                    break;
                case CHINESE:
                    captcha = new ChineseCaptcha(captchaEntity.getWidth(), captchaEntity.getHeight());
                    captcha.setLen(captchaEntity.getLength());
                    break;
                case CHINESE_GIF:
                    captcha = new ChineseGifCaptcha(captchaEntity.getWidth(), captchaEntity.getHeight());
                    captcha.setLen(captchaEntity.getLength());
                    break;
                case GIF:
                    captcha = new GifCaptcha(captchaEntity.getWidth(), captchaEntity.getHeight());
                    captcha.setLen(captchaEntity.getLength());
                    break;
                case SPEC:
                    captcha = new SpecCaptcha(captchaEntity.getWidth(), captchaEntity.getHeight());
                    captcha.setLen(captchaEntity.getLength());
                default:
                    System.out.println("验证码配置信息错误！");
            }
        }
        captcha.setFont(new Font(captchaEntity.getFontFamily(), captchaEntity.getFontStyle(), captchaEntity.getFontSize()));
        return captcha;
    }
}
