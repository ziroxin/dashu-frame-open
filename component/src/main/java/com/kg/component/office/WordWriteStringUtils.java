package com.kg.component.office;

import com.kg.component.office.dto.WordStrFormatDTO;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word 字符串 - 操作工具类
 *
 * @author ziro
 * @date 2024/1/23 13:30
 */
public class WordWriteStringUtils {

    /**
     * 匹配 ${key} 写入文本内容
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrByKey(XWPFDocument doc, String key, String content, boolean isAppend) {
        return writeStrByKey(doc, key, content, null, isAppend);
    }

    /**
     * 匹配 ${key} 写入文本内容（自定义格式）
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @param format  常用格式设置
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrByKey(XWPFDocument doc, String key, String content,
                                              WordStrFormatDTO format, boolean isAppend) {
        key = "${" + key + "}";
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            StringBuilder sb = new StringBuilder();
            for (XWPFRun run : paragraph.getRuns()) {
                sb.append(run.getText(0));// 本段落内容关联起来
            }
            // 匹配key
            String text = sb.toString();
            Pattern pattern = Pattern.compile(Pattern.quote(key));
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // 移除文档中包含${key}的内容
                for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {
                    paragraph.removeRun(i);
                }
                // 插入替换后的内容
                XWPFRun run = paragraph.getRuns().get(0);
                text = matcher.replaceAll(content);// 替换段落中${key}内容
                run.setText(text, 0);
                if (format != null) {
                    // 设置样式
                    runTextFormat(run, format);
                }
                if (isAppend) {
                    // 追加内容，把原标签写入
                    paragraph.createRun().setText(key);
                }
                return paragraph;
            }
        }
        return null;
    }

    /**
     * 另起一行：匹配 ${key} 写入文本内容
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrNewline(XWPFDocument doc, String key, String content, boolean isAppend) {
        return writeStrNewline(doc, key, content, null, isAppend);
    }


    /**
     * 另起一行：匹配 ${key} 写入文本内容（自定义格式）
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @param format  常用格式设置
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrNewline(XWPFDocument doc, String key, String content,
                                                WordStrFormatDTO format, boolean isAppend) {
        key = "${" + key + "}";
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            StringBuilder sb = new StringBuilder();
            for (XWPFRun run : paragraph.getRuns()) {
                sb.append(run.getText(0));// 本段落内容关联起来
            }
            // 匹配key
            String text = sb.toString();
            Pattern pattern = Pattern.compile(Pattern.quote(key));
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // 移除文档中包含${key}的内容
                for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {
                    paragraph.removeRun(i);
                }
                // 插入替换后的内容
                XWPFRun run = paragraph.getRuns().get(0);
                run.setText(matcher.replaceAll(""), 0);// 先把原内容写入，替换掉${key}
                if (StringUtils.hasText(run.text())) {
                    // 创建新段落，写入新内容
                    XmlCursor cursor1 = paragraph.getCTP().newCursor();
                    cursor1.toNextSibling();
                    XWPFParagraph newParagraph = doc.insertNewParagraph(cursor1);
                    XWPFRun newRun = newParagraph.createRun();
                    newRun.setText(content, 0);
                    paragraph = newParagraph;
                    run = newRun;
                } else {
                    // 原内容为空，则直接写入
                    run.setText(content, 0);
                }
                if (format != null) {
                    // 设置样式
                    runTextFormat(run, format);
                }
                if (isAppend) {
                    // 追加内容，在新段落把原标签写入
                    XmlCursor cursor2 = paragraph.getCTP().newCursor();
                    cursor2.toNextSibling();
                    doc.insertNewParagraph(cursor2).createRun().setText(key);
                }
                return paragraph;
            }
        }
        return null;
    }

    /**
     * 写入文本格式
     */
    public static void runTextFormat(XWPFRun run, WordStrFormatDTO format) {
        // 如果有格式，则设置格式
        if (null != format.getBold()) {
            run.setBold(format.getBold());
        }
        if (null != format.getItalic()) {
            run.setItalic(format.getItalic());
        }
        if (null != format.getUnderline() && format.getUnderline()) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }
        if (StringUtils.hasText(format.getFontFamily())) {
            run.setFontFamily(format.getFontFamily());
        }
        if (null != format.getFontSize()) {
            run.setFontSize(format.getFontSize());
        }
        if (StringUtils.hasText(format.getColor())) {
            run.setColor(format.getColor());
        }
    }

    /**
     * 获取文本格式
     */
    private static WordStrFormatDTO getFormat(XWPFRun run) {
        WordStrFormatDTO format = new WordStrFormatDTO();
        format.setBold(run.isBold());
        format.setItalic(run.isItalic());
        format.setUnderline(run.getUnderline() == UnderlinePatterns.SINGLE);
        format.setFontFamily(run.getFontFamily());
        if (run.getFontSize() > 0) {
            format.setFontSize(run.getFontSize());
        }
        format.setColor(run.getColor());
        return format;
    }
}
