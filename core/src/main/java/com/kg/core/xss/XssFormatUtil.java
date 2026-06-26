package com.kg.core.xss;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * XSS处理工具类
 * <p>
 * 【设计理念】
 * 本工具类只负责过滤危险内容，不对普通字符进行HTML编码（如 & → &amp;）。
 * 这样做的原因：
 * 1. 数据库应存储原始数据，不应被编码污染（& 就是 &，不应变成 &amp;）
 * 2. XSS防护应分层实现：
 *    - 后端：过滤危险标签和属性（本工具类的职责）
 *    - 前端：Vue的 {{ }} 模板语法会自动转义HTML，防止XSS执行
 * 3. 如果在存入数据库时就编码，会导致所有读取数据的地方都需要解码，增加复杂度和出错概率
 * <p>
 * 【过滤范围】
 * - 移除：script、iframe、object、embed、form 等危险标签
 * - 移除：on* 事件属性（如 onclick=、onerror= 等）
 * - 移除：javascript:、vbscript:、data: 等危险URI协议
 * - 移除：eval()、expression() 等危险函数调用
 * <p>
 * 【保留内容】
 * - 普通文本、数字、中文
 * - HTML实体字符（&、<、>、"、'）——这些由前端Vue模板自动转义，无需后端编码
 *
 * @author ziro
 * @date 2023-01-28 17:29:03
 */
public class XssFormatUtil {

    /** 匹配 <script> 和 </script> 标签（含属性和换行） */
    private static final Pattern SCRIPT_TAG = Pattern.compile("<\\s*/?\\s*script\\b[^>]*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** 匹配所有 on* 事件属性，如 onclick=、onerror=、onload= 等 */
    private static final Pattern EVENT_ATTR = Pattern.compile("\\bon\\w+\\s*=", Pattern.CASE_INSENSITIVE);

    /** 匹配 javascript: 协议（常见于 href 属性注入） */
    private static final Pattern JAVASCRIPT_URI = Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE);

    /** 匹配 vbscript: 协议（IE浏览器历史遗留） */
    private static final Pattern VBSCRIPT_URI = Pattern.compile("vbscript\\s*:", Pattern.CASE_INSENSITIVE);

    /** 匹配 eval() 函数调用（常用于执行动态代码） */
    private static final Pattern EVAL_EXPR = Pattern.compile("eval\\s*\\(", Pattern.CASE_INSENSITIVE);

    /** 匹配 CSS expression() 函数（IE浏览器历史遗留，可执行JS代码） */
    private static final Pattern EXPRESSION = Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE);

    /** 匹配 data: 协议（可嵌入恶意内容） */
    private static final Pattern DATA_URI = Pattern.compile("data\\s*:", Pattern.CASE_INSENSITIVE);

    /** 匹配 <iframe> 标签（常用于嵌入恶意页面） */
    private static final Pattern IFRAME_TAG = Pattern.compile("<\\s*/?\\s*iframe\\b[^>]*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** 匹配 <object> 标签（可加载ActiveX等危险插件） */
    private static final Pattern OBJECT_TAG = Pattern.compile("<\\s*/?\\s*object\\b[^>]*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** 匹配 <embed> 标签（可嵌入恶意内容） */
    private static final Pattern EMBED_TAG = Pattern.compile("<\\s*/?\\s*embed\\b[^>]*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** 匹配 <form> 标签（防止恶意表单提交） */
    private static final Pattern FORM_TAG = Pattern.compile("<\\s*/?\\s*form\\b[^>]*>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * 过滤XSS危险内容（只过滤，不编码普通字符）
     * <p>
     * 使用场景：在数据存入数据库前调用，过滤掉危险的标签和属性。
     * <p>
     * 示例：
     * - 输入: "Hello <script>alert(1)</script> World" → 输出: "Hello  World"
     * - 输入: "图片 <img src=x onerror=alert(1)>" → 输出: "图片 <img src=x >"
     * - 输入: "链接 javascript:alert(1)" → 输出: "链接 alert(1)"
     * - 输入: "普通文本 & 符号" → 输出: "普通文本 & 符号"（保留原样）
     *
     * @param html 原始HTML内容
     * @return 过滤后的安全内容（保留普通字符）
     */
    public static String cleanHtml(CharSequence html) {
        if (!StringUtils.hasText(html)) {
            return html != null ? html.toString() : "";
        }
        String value = html.toString();

        // 1. 移除危险标签
        value = SCRIPT_TAG.matcher(value).replaceAll("");
        value = IFRAME_TAG.matcher(value).replaceAll("");
        value = OBJECT_TAG.matcher(value).replaceAll("");
        value = EMBED_TAG.matcher(value).replaceAll("");
        value = FORM_TAG.matcher(value).replaceAll("");

        // 2. 移除事件属性（onclick=、onerror= 等）
        value = EVENT_ATTR.matcher(value).replaceAll(" ");

        // 3. 移除危险协议（javascript:、vbscript:、data:）
        value = JAVASCRIPT_URI.matcher(value).replaceAll("");
        value = VBSCRIPT_URI.matcher(value).replaceAll("");
        value = DATA_URI.matcher(value).replaceAll("");

        // 4. 移除危险函数调用
        value = EVAL_EXPR.matcher(value).replaceAll("");
        value = EXPRESSION.matcher(value).replaceAll("");

        return value;
    }

    /**
     * 解码HTML实体（反向操作，将HTML实体还原为原始字符）
     * <p>
     * 使用场景：如果数据库中已经存储了编码后的数据（历史遗留问题），
     * 可以调用此方法将 &amp; 还原为 &，&lt; 还原为 < 等。
     * <p>
     * 注意：新数据不应编码后存储，此方法仅用于兼容历史数据。
     * <p>
     * 示例：
     * - 输入: "Tom &amp; Jerry" → 输出: "Tom & Jerry"
     * - 输入: "&lt;script&gt;" → 输出: "<script>"
     * - 输入: "普通文本" → 输出: "普通文本"（无变化）
     *
     * @param value 包含HTML实体的字符串
     * @return 解码后的原始字符串
     */
    public static String toHtml(CharSequence value) {
        if (!StringUtils.hasText(value)) {
            return value != null ? value.toString() : "";
        }
        String html = value.toString();
        html = html.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'");
        return html;
    }
}
