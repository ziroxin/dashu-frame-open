package com.kg.component.pdf;

import cn.hutool.core.io.FileUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 图片转PDF工具类
 *
 * @author ziro
 * @date 2024/5/29 9:52
 */
public class ImgToPdfUtils {
    /**
     * 一张图片转PDF
     *
     * @param imgPath 图片路径
     */
    public static FileDTO toPdf(String imgPath) throws IOException {
        return toPdf(imgPath, null, null);
    }

    /**
     * 一张图片转PDF
     *
     * @param imgPath 图片路径
     * @param width   pdf页面宽度
     * @param height  pdf页面高度
     */
    public static FileDTO toPdf(String imgPath, Integer width, Integer height) throws IOException {
        String s = FilePathConfig.switchSavePath(imgPath);
        return toPdf(s, s.substring(0, s.lastIndexOf(".")) + ".pdf", width, height);
    }


    /**
     * 一张图片转PDF
     *
     * @param imgPath     图片路径
     * @param pdfSavePath PDF保存路径
     */
    public static FileDTO toPdf(String imgPath, String pdfSavePath) throws IOException {
        return toPdf(imgPath, pdfSavePath, null, null);
    }

    /**
     * 一张图片转PDF
     *
     * @param imgPath     图片路径
     * @param pdfSavePath PDF保存路径
     * @param width       pdf页面宽度
     * @param height      pdf页面高度
     */
    public static FileDTO toPdf(String imgPath, String pdfSavePath, Integer width, Integer height)
            throws IOException {
        // 读取图片文件，生成文档对象
        PDDocument document = new PDDocument();
        write(document, imgPath, width, height);
        // 保存PDF文件
        FileUtil.mkParentDirs(pdfSavePath);
        document.save(pdfSavePath);
        document.close();
        // 返回文件信息
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileUrl(FilePathConfig.switchUrl(pdfSavePath));
        fileDTO.setFileName(pdfSavePath.substring(pdfSavePath.lastIndexOf(File.separator) + 1));
        fileDTO.setFileSize(new File(pdfSavePath).length());
        fileDTO.setFileExtend("pdf");
        fileDTO.setFileOldName(imgPath.substring(imgPath.lastIndexOf(File.separator) + 1));
        return fileDTO;
    }

    /**
     * 多张图片转PDF
     *
     * @param imgPaths    图片路径数组
     * @param pdfSavePath PDF保存路径
     */
    public static FileDTO listToPdf(String[] imgPaths, String pdfSavePath) throws IOException {
        return listToPdf(imgPaths, pdfSavePath, null, null);
    }

    /**
     * 多张图片转PDF
     *
     * @param imgPaths    图片路径数组
     * @param pdfSavePath PDF保存路径
     * @param width       pdf页面宽度
     * @param height      pdf页面高度
     */
    public static FileDTO listToPdf(String[] imgPaths, String pdfSavePath, Integer width, Integer height)
            throws IOException {
        return listToPdf(Arrays.asList(imgPaths), pdfSavePath, width, height);
    }

    /**
     * 多张图片转PDF
     *
     * @param imgPathList 图片路径列表
     * @param pdfSavePath PDF保存路径
     */
    public static FileDTO listToPdf(List<String> imgPathList, String pdfSavePath) throws IOException {
        return listToPdf(imgPathList, pdfSavePath, null, null);
    }

    /**
     * 多张图片转PDF
     *
     * @param imgPathList 图片路径列表
     * @param pdfSavePath PDF保存路径
     * @param width       pdf页面宽度
     * @param height      pdf页面高度
     */
    public static FileDTO listToPdf(List<String> imgPathList, String pdfSavePath, Integer width, Integer height)
            throws IOException {
        // 读取图片文件，生成文档对象
        PDDocument document = new PDDocument();
        for (String imgPath : imgPathList) {
            write(document, imgPath, width, height);
        }
        // 保存PDF文件
        FileUtil.mkParentDirs(pdfSavePath);
        document.save(pdfSavePath);
        document.close();
        // 返回文件信息
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileUrl(FilePathConfig.switchUrl(pdfSavePath));
        String fileName = pdfSavePath.substring(pdfSavePath.lastIndexOf(File.separator) + 1);
        fileDTO.setFileName(fileName);
        fileDTO.setFileSize(new File(pdfSavePath).length());
        fileDTO.setFileExtend("pdf");
        fileDTO.setFileOldName(fileName);
        return fileDTO;
    }

    /**
     * (private) 写入图片到PDF文档
     */
    private static void write(PDDocument document, String imgPath, float maxWidth, float maxHeight) throws IOException {
        BufferedImage image = ImageIO.read(new File(imgPath));

        float width = image.getWidth();
        float height = image.getHeight();

        if (width > maxWidth || height > maxHeight) {
            float widthRatio = maxWidth / width;
            float heightRatio = maxHeight / height;
            float ratio = Math.min(widthRatio, heightRatio);
            width *= ratio;
            height *= ratio;
        }

        // 创建页面尺寸
        PDPage page = new PDPage(new PDRectangle(maxWidth, maxHeight));
        document.addPage(page);
        // 创建内容流
        PDImageXObject pdImage = PDImageXObject.createFromFile(imgPath, document);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        // 图片位置，左上角为原点
        contentStream.drawImage(pdImage, 0, maxHeight - height, width, height);
        contentStream.close();
    }

}
