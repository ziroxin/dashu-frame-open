package com.kg.component.office;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Word 图片 - 操作工具类
 *
 * @author ziro
 * @date 2024/1/23 13:30
 */
public class WordWriteImageUtils {

    /**
     * 图片写入，自动适配宽度（最大宽度600px，最大高度930px）
     *
     * @param doc      待操作的文档
     * @param keyStr   要匹配的key
     * @param imgFile  图片文件（支持jpg/jpeg/png/gif/tiff/bpm）
     * @param isAppend 是否追加到当前段落（插入图片后保留标签${keyStr}）
     * @return 返回当前段落
     */
    public static XWPFParagraph imageInTableAutoSize(XWPFDocument doc, String keyStr, File imgFile, boolean isAppend) {
        try {
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
            return writeInTable(doc, keyStr, imgFile, getPictureType(imgFile.getName()), width, height, isAppend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片写入，自定义宽高
     *
     * @param doc      待操作的文档
     * @param keyStr   要匹配的key
     * @param imgFile  图片（支持jpg/jpeg/png/gif/tiff/bpm）
     * @param width    宽度
     * @param height   高度
     * @param isAppend 是否追加到当前段落（插入图片后保留标签${keyStr}）
     * @return 返回当前段落
     */
    public static XWPFParagraph imageInTable(XWPFDocument doc, String keyStr, File imgFile,
                                             int width, int height, boolean isAppend) {
        return writeInTable(doc, keyStr, imgFile, getPictureType(imgFile.getName()), width, height, isAppend);
    }

    /**
     * 匹配 ${key} 写入表格
     * 默认创建一个：宽度100%，居中显示的表格
     *
     * @param doc      所操作的文档
     * @param keyStr   要匹配的key
     * @param imgFile  图片文件流
     * @param imgType  图片类型XWPFDocument常量，如：XWPFDocument.PICTURE_TYPE_JPEG
     * @param width    图片宽度，单位：像素（页面宽度600px）
     * @param height   图片高度，单位：像素（页面最高度930px）
     * @param isAppend 是否追加到当前段落
     * @return 返回当前段落
     */
    private static XWPFParagraph writeInTable(XWPFDocument doc, String keyStr, File imgFile,
                                              int imgType, int width, int height, boolean isAppend) {
        try {
            String key = "${" + keyStr + "}";
            // 遍历表格，每个单元格遍历段落
            List<XWPFTable> tables = doc.getTables();
            for (XWPFTable table : tables) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        XWPFParagraph newParagraph = WordWriteStringUtils.write(cell.getParagraphs(), key, "", null, false);
                        if (newParagraph != null) {
                            XWPFRun run = newParagraph.createRun();
                            // 插入图片
                            run.addPicture(new FileInputStream(imgFile), imgType, null,
                                    Units.toEMU(Units.pixelToPoints(width)), Units.toEMU(Units.pixelToPoints(height)));
                            newParagraph.setAlignment(ParagraphAlignment.CENTER);
                            // 是否追加
                            if (isAppend) {
                                cell.addParagraph().createRun().setText("${" + keyStr + "}");
                            }
                            return newParagraph;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片写入，自动适配宽度（最大宽度600px，最大高度930px）
     *
     * @param doc      待操作的文档
     * @param keyStr   要匹配的key
     * @param imgFile  图片文件（支持jpg/jpeg/png/gif/tiff/bpm）
     * @param isAppend 是否追加到当前段落（插入图片后保留标签${keyStr}）
     * @return 返回当前段落
     */
    public static XWPFParagraph imageWriteAutoSize(XWPFDocument doc, String keyStr, File imgFile, boolean isAppend) {
        try {
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
            return write(doc, keyStr, imgFile, getPictureType(imgFile.getName()), width, height, isAppend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片写入，自定义宽高
     *
     * @param doc      待操作的文档
     * @param keyStr   要匹配的key
     * @param imgFile  图片（支持jpg/jpeg/png/gif/tiff/bpm）
     * @param width    宽度
     * @param height   高度
     * @param isAppend 是否追加到当前段落（插入图片后保留标签${keyStr}）
     * @return 返回当前段落
     */
    public static XWPFParagraph imageWrite(XWPFDocument doc, String keyStr, File imgFile,
                                           int width, int height, boolean isAppend) {
        return write(doc, keyStr, imgFile, getPictureType(imgFile.getName()), width, height, isAppend);
    }

    /**
     * 匹配 ${key} 写入表格
     * 默认创建一个：宽度100%，居中显示的表格
     *
     * @param doc      所操作的文档
     * @param keyStr   要匹配的key
     * @param imgFile  图片文件流
     * @param imgType  图片类型XWPFDocument常量，如：XWPFDocument.PICTURE_TYPE_JPEG
     * @param width    图片宽度，单位：像素（页面宽度600px）
     * @param height   图片高度，单位：像素（页面最高度930px）
     * @param isAppend 是否追加到当前段落
     * @return 返回当前段落
     */
    private static XWPFParagraph write(XWPFDocument doc, String keyStr, File imgFile,
                                       int imgType, int width, int height, boolean isAppend) {
        try {
            // 另起一行，并获取当前段落
            XWPFParagraph currentParagraph = WordWriteStringUtils.writeStrNewline(doc, keyStr, "", null, false);
            // 获取光标的位置
            XmlCursor cursor = currentParagraph.getCTP().newCursor();
            // 插入新段落
            XWPFParagraph newParagraph = doc.insertNewParagraph(cursor);
            XWPFRun run = newParagraph.createRun();
            // 插入图片
            run.addPicture(new FileInputStream(imgFile), imgType, null,
                    Units.toEMU(Units.pixelToPoints(width)), Units.toEMU(Units.pixelToPoints(height)));
            newParagraph.setAlignment(ParagraphAlignment.CENTER);
            // 是否追加
            if (isAppend) {
                currentParagraph.createRun().setText("${" + keyStr + "}");
            }
            return newParagraph;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据扩展名，获取word需要的文件类型
     *
     * @param filename 文件名
     * @return 图片文件类型
     */
    private static int getPictureType(String filename) {
        String extension = filename.toLowerCase().substring(filename.lastIndexOf(".") + 1);
        switch (extension) {
            case "jpg":
                return XWPFDocument.PICTURE_TYPE_JPEG;
            case "jpeg":
                return XWPFDocument.PICTURE_TYPE_JPEG;
            case "png":
                return XWPFDocument.PICTURE_TYPE_PNG;
            case "gif":
                return XWPFDocument.PICTURE_TYPE_GIF;
            case "tiff":
                return XWPFDocument.PICTURE_TYPE_TIFF;
            case "bpm":
                return XWPFDocument.PICTURE_TYPE_BMP;
            default:
                return XWPFDocument.PICTURE_TYPE_JPEG;
        }
    }

}
