package com.kg.component.office;

import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import com.kg.component.file.utils.DownloadNetFileUtils;
import com.kg.component.office.dto.WordStrFormatDTO;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word写入html - 操作工具类
 *
 * @author ziro
 * @date 2024/7/31 8:44
 */
@Component
public class WordWriteHtmlUtils {
    private static String API_BASE_URL;

    @Value("${com.kg.apiBaseUrl}")
    public void setApiBaseUrl(String apiBaseUrl) {
        WordWriteHtmlUtils.API_BASE_URL = apiBaseUrl;
    }

    /**
     * 匹配 ${key} 写入html内容
     *
     * @param doc  所操作文档
     * @param key  要匹配的key
     * @param html 写入html内容
     * @return 返回当前段落
     */
    public static XWPFParagraph writeByKey(XWPFDocument doc, String key, String html, boolean isAppend) {
        return writeByKey(doc, key, html, null, isAppend);
    }

    /**
     * 匹配 ${key} 写入文本内容（自定义格式）
     *
     * @param doc    所操作文档
     * @param key    要匹配的key
     * @param html   写入html内容
     * @param format 常用格式设置（只有文字格式，图片自动大小）
     * @return 返回当前段落
     */
    public static XWPFParagraph writeByKey(XWPFDocument doc, String key, String html,
                                           WordStrFormatDTO format, boolean isAppend) {
        key = "${" + key + "}";
        // 遍历段落
        List<XWPFParagraph> paragraphs = doc.getParagraphs();
        // 1. 正常标签
        XWPFParagraph result = write(doc, paragraphs, key, html, format, isAppend);
        if (result == null) {
            // 遍历表格，每个单元格遍历段落
            List<XWPFTable> tables = doc.getTables();
            for (XWPFTable table : tables) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        // 2. 表格单元格内标签
                        result = write(doc, cell.getParagraphs(), key, html, format, isAppend);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    static XWPFParagraph write(XWPFDocument doc, List<XWPFParagraph> paragraphs, String key, String html,
                               WordStrFormatDTO format, boolean isAppend) {
        for (XWPFParagraph paragraph : paragraphs) {
            Pattern pattern = Pattern.compile(Pattern.quote(key));
            StringBuilder sb = new StringBuilder();
            for (XWPFRun run : paragraph.getRuns()) {
                sb.append(run.getText(0));// 本段落内容关联起来
            }
            // 匹配key
            String text = sb.toString();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // 移除文档中包含${key}的内容
                for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {
                    paragraph.removeRun(i);
                }
                // 插入替换后的内容
                XWPFRun run = paragraph.getRuns().get(0);
                run.setText(matcher.replaceAll(""), 0);// 替换段落中${key}内容
                if (format != null) {
                    // 设置样式
                    runTextFormat(run, format);
                }
                // 写入html内容
                XWPFParagraph newParagraph = runHtmlWrite(doc, paragraph, html, format);
                if (isAppend) {
                    // 追加内容，把原标签写入
                    newParagraph.createRun().setText(key);
                }
                return newParagraph;
            }
        }
        return null;
    }

    private static XWPFParagraph runHtmlWrite(XWPFDocument doc, XWPFParagraph xwpfParagraph, String html,
                                              WordStrFormatDTO format) {
        // 转换html为dom
        Document jsoupDoc = Jsoup.parse(html);
        // 取出所有p标签
        Elements elements = jsoupDoc.select("p");
        // 遍历p标签，写入段落内容
        for (Element element : elements) {
            // 1. 若有图片，先写入图片
            Elements images = element.select("img");
            if (!images.isEmpty()) {
                for (Element image : images) {
                    String imgSrc = image.attr("src");
                    try {
                        File imgFile = getImgFile(imgSrc);
                        BufferedImage imgRead = ImageIO.read(imgFile);
                        int width = imgRead.getWidth();
                        int height = imgRead.getHeight();
                        if (width > 600) {
                            height = 600 * height / width;
                            width = 600;
                        }
                        if (height > 930) {
                            width = 930 * width / height;
                            height = 930;
                        }
                        int imgType = getPictureType(imgSrc);
                        xwpfParagraph.createRun().addPicture(new FileInputStream(imgFile), imgType, null,
                                Units.toEMU(Units.pixelToPoints(width)), Units.toEMU(Units.pixelToPoints(height)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // 2. 写入段内文本内容
            String text = element.text();
            if (StringUtils.hasText(text)) {
                XWPFRun run = xwpfParagraph.createRun();
                run.setText(text);
                if (format != null) {
                    // 设置样式
                    runTextFormat(run, format);
                }
            }
            XmlCursor cursor1 = xwpfParagraph.getCTP().newCursor();
            cursor1.toNextSibling();
            xwpfParagraph = doc.insertNewParagraph(cursor1);
        }
        // 3. 写入完成，返回最新段落
        return xwpfParagraph;
    }

    /**
     * 获取img文件
     *
     * @param imgSrc 图片地址
     * @return {@link File }
     */
    private static File getImgFile(String imgSrc) throws IOException {
        if (imgSrc.startsWith(API_BASE_URL)) {
            // 本地图片
            imgSrc = "/" + imgSrc.replace(API_BASE_URL, "").replaceAll("//", "/");
            String savePath = FilePathConfig.switchSavePath(imgSrc);
            return new File(savePath);
        } else if (imgSrc.startsWith("http")) {
            // 网络图片
            FileDTO tempFile = DownloadNetFileUtils.download(imgSrc, "tempImgFile");
            String savePath = FilePathConfig.switchSavePath(tempFile.getFileUrl());
            return new File(savePath);
        } else {
            // 本地图片
            return new File(imgSrc);
        }
    }


    /**
     * 写入文本格式
     */
    private static void runTextFormat(XWPFRun run, WordStrFormatDTO format) {
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
     * 获取图片扩展名
     *
     * @param imgSrc 图片地址
     * @return word中图片类型
     */
    private static int getPictureType(String imgSrc) {
        String extension = imgSrc.substring(imgSrc.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "png":
                return XWPFDocument.PICTURE_TYPE_PNG;
            case "jpeg":
                return XWPFDocument.PICTURE_TYPE_JPEG;
            case "jpg":
                return XWPFDocument.PICTURE_TYPE_JPEG;
            case "gif":
                return XWPFDocument.PICTURE_TYPE_GIF;
            default:
                return XWPFDocument.PICTURE_TYPE_PNG;
        }
    }
}
