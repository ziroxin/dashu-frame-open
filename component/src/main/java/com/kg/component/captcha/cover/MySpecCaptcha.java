package com.kg.component.captcha.cover;

import com.kg.component.captcha.CaptchaConfig;
import com.wf.captcha.SpecCaptcha;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 扩展重写 SpecCaptcha
 *
 * <p>
 * 1. 重写 graphicsImage 方法，增加自定义背景色
 *
 * @author ziro
 * @date 2024/2/21 9:21
 */
public class MySpecCaptcha extends SpecCaptcha {
    private CaptchaConfig config;

    public MySpecCaptcha(int width, int height, CaptchaConfig captchaConfig) {
        super(width, height);
        this.config = captchaConfig;
    }

    /**
     * 生成验证码
     *
     * @param out 输出流
     * @return 是否成功
     */
    @Override
    public boolean out(OutputStream out) {
        return graphicsImage(textChar(), out);
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
            // 画干扰线
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            drawBesselLine(1, g2d);
            // 画字符串
            g2d.setFont(getFont());
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int fW = width / strs.length;  // 每一个字符所占的宽度
            int fSp = (fW - (int) fontMetrics.getStringBounds("W", g2d).getWidth()) / 2;  // 字符的左右边距
            for (int i = 0; i < strs.length; i++) {
                g2d.setColor(color());
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
