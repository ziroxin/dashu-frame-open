package com.kg.component.captcha;

import com.wf.captcha.ArithmeticCaptcha;

/**
 * 算数规则验证码 - 生成器
 *
 * @author ziro
 * @date 2022-12-27 11:25:11
 */
public class CaptchaFixedArithmetic extends ArithmeticCaptcha {

    public CaptchaFixedArithmetic(int width, int height) {
        super(width, height);
    }

    /**
     * 生成
     */
    @Override
    protected char[] alphas() {
        // 生成随机数字和运算符
        int n1 = num(1, 10), n2 = num(1, 10);
        int opt = num(3);

        // 计算结果
        int res = new int[]{n1 + n2, n1 - n2, n1 * n2}[opt];
        // 转换为字符运算符
        char optChar = "+-x".charAt(opt);

        this.setArithmeticString(String.format("%s%c%s=?", n1, optChar, n2));
        this.chars = String.valueOf(res);

        return chars.toCharArray();
    }

}
