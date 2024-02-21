package com.kg.component.captcha.cover;

import com.kg.component.captcha.CaptchaConfig;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 扩展重写 ArithmeticCaptcha
 *
 * <p>
 * 1. 只保留加法和乘法，去掉减法（不要负数）
 * 2. 重写 graphicsImage 方法，增加自定义背景色
 *
 * @author ziro
 * @date 2024/2/21 9:25
 */
public class MyArithmeticCaptcha extends ArithmeticCaptcha {
    private CaptchaConfig config;

    public MyArithmeticCaptcha(int width, int height, CaptchaConfig captchaConfig) {
        super(width, height);
        this.config = captchaConfig;
    }

    /**
     * 生成
     */
    @Override
    protected char[] alphas() {
        // 无减法（为了不出现负数，只保留+x）
        return alphasNoSubtract();
        // 默认：+-x
        //return alphasDefault();
    }

    private char[] alphasDefault() {
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

    private char[] alphasNoSubtract() {
        // 生成随机数字和运算符
        int n1 = num(1, 10), n2 = num(1, 10);
        int opt = num(2);

        // 计算结果
        int res = new int[]{n1 + n2, n1 * n2}[opt];
        // 转换为字符运算符
        // 注意：这里可配置+-x三种算数方式
        //      为了不出现负数，只保留+x
        char optChar = "+x".charAt(opt);

        this.setArithmeticString(String.format("%s%c%s=?", n1, optChar, n2));
        this.chars = String.valueOf(res);

        return chars.toCharArray();
    }

    /**
     * 生成验证码
     *
     * @param out 输出流
     * @return 是否成功
     */
    @Override
    public boolean out(OutputStream out) {
        checkAlpha();
        return graphicsImage(getArithmeticString().toCharArray(), out);
    }

    /**
     * 生成验证码图形
     *
     * @param strs 验证码
     * @param out  输出流
     * @return boolean
     */
    private boolean graphicsImage(char[] strs, OutputStream out) {
        try {
            BufferedImage bi;
            Graphics2D g2d;
            // 扩展开发：增加自定义背景色 @author ziro 2024/2/21 11:26
            if (null != config.getBgColor() && StringUtils.hasText(config.getBgColor())) {
                // 填充背景色
                bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                g2d = (Graphics2D) bi.getGraphics();
                g2d.setColor(new Color(Integer.parseInt(config.getBgColor(), 16)));
                g2d.fillRect(0, 0, width, height);
            } else {
                // 透明背景色
                bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                g2d = (Graphics2D) bi.getGraphics();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                g2d.fillRect(0, 0, width, height);
                g2d.setComposite(AlphaComposite.Src);
            }
            // 抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 画干扰圆
            drawOval(2, g2d);
            // 画字符串
            g2d.setFont(getFont());
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int fW = width / strs.length;  // 每一个字符所占的宽度
            int fSp = (fW - (int) fontMetrics.getStringBounds("8", g2d).getWidth()) / 2;  // 字符的左右边距
            for (int i = 0; i < strs.length; i++) {
                if (null != config.getFontColor() && StringUtils.hasText(config.getFontColor())) {
                    // 字体设置颜色
                    g2d.setColor(new Color(Integer.parseInt(config.getFontColor(), 16)));
                } else {
                    // 字体随机颜色
                    g2d.setColor(color());
                }
                int fY = height - ((height - (int) fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight()) >> 1);  // 文字的纵坐标
                g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
            }
            g2d.dispose();
            ImageIO.write(bi, "png", out);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
