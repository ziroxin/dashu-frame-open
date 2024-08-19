package com.kg.component.pdf;

import cn.hutool.core.io.FileUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import com.kg.component.utils.GuidUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.util.StringUtils;

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
     * @param imgUrl 图片路径
     */
    public static FileDTO toPdf(String imgUrl) throws IOException {
        return toPdf(imgUrl, null, null, null);
    }

    /**
     * 一张图片转PDF
     *
     * @param imgUrl 图片路径
     * @param width  pdf页面宽度
     * @param height pdf页面高度
     */
    public static FileDTO toPdf(String imgUrl, Integer width, Integer height) throws IOException {
        return toPdf(imgUrl, null, width, height);
    }


    /**
     * 一张图片转PDF
     *
     * @param imgUrl    图片路径
     * @param outFolder PDF保存文件夹（为空时，默认保存到源文件同目录）
     */
    public static FileDTO toPdf(String imgUrl, String outFolder) throws IOException {
        return toPdf(imgUrl, outFolder, null, null);
    }

    /**
     * 一张图片转PDF
     *
     * @param imgUrl    图片路径
     * @param outFolder PDF保存文件夹（为空时，默认保存到源文件同目录）
     * @param width     pdf页面宽度
     * @param height    pdf页面高度
     */
    public static FileDTO toPdf(String imgUrl, String outFolder, Integer width, Integer height)
            throws IOException {
        // 读取图片文件，生成文档对象
        PDDocument document = new PDDocument();
        String savePath = FilePathConfig.switchSavePath(imgUrl);
        write(document, savePath, width, height);
        // 保存PDF文件
        String imgName = savePath.substring(savePath.lastIndexOf("/"));
        String pdfSavePath = StringUtils.hasText(outFolder)
                ? FilePathConfig.SAVE_PATH + "/" + outFolder + "/" + imgName.substring(0, imgName.lastIndexOf(".")) + ".pdf"
                : savePath.substring(0, savePath.lastIndexOf(".")) + ".pdf";
        pdfSavePath = pdfSavePath.replaceAll("//", "/");
        FileUtil.mkParentDirs(pdfSavePath);
        document.save(pdfSavePath);
        document.close();
        // 返回文件信息
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileUrl(FilePathConfig.switchUrl(pdfSavePath));
        fileDTO.setFileName(pdfSavePath.substring(pdfSavePath.lastIndexOf("/") + 1));
        fileDTO.setFileSize(new File(pdfSavePath).length());
        fileDTO.setFileExtend("pdf");
        return fileDTO;
    }

    /**
     * 多张图片转PDF
     *
     * @param imgUrls   图片路径数组
     * @param outFolder PDF保存文件夹
     */
    public static FileDTO listToPdf(String[] imgUrls, String outFolder) throws IOException {
        return listToPdf(Arrays.asList(imgUrls), outFolder, null, null);
    }

    /**
     * 多张图片转PDF
     *
     * @param imgUrls   图片路径数组
     * @param outFolder PDF保存文件夹
     * @param width     pdf页面宽度
     * @param height    pdf页面高度
     */
    public static FileDTO listToPdf(String[] imgUrls, String outFolder, Integer width, Integer height)
            throws IOException {
        return listToPdf(Arrays.asList(imgUrls), outFolder, width, height);
    }

    /**
     * 多张图片转PDF
     *
     * @param imgUrlList 图片路径列表
     * @param outFolder  PDF保存文件夹
     * @param outFolder  PDF保存文件夹
     */
    public static FileDTO listToPdf(List<String> imgUrlList, String outFolder) throws IOException {
        return listToPdf(imgUrlList, outFolder, null, null);
    }

    /**
     * 多张图片转PDF
     *
     * @param imgUrlList 图片路径列表
     * @param outFolder  PDF保存路径
     * @param width      pdf页面宽度
     * @param height     pdf页面高度
     */
    public static FileDTO listToPdf(List<String> imgUrlList, String outFolder, Integer width, Integer height)
            throws IOException {
        // 读取图片文件，生成文档对象
        PDDocument document = new PDDocument();
        for (String imgUrl : imgUrlList) {
            // 写入图片到PDF文档
            String imgPath = FilePathConfig.switchSavePath(imgUrl);
            write(document, imgPath, width, height);
        }
        // 保存PDF文件
        String pdfSavePath = FilePathConfig.SAVE_PATH + "/" + outFolder + "/" + GuidUtils.getUuid32() + ".pdf";
        pdfSavePath = pdfSavePath.replaceAll("//", "/");
        FileUtil.mkParentDirs(pdfSavePath);
        document.save(pdfSavePath);
        document.close();
        // 返回文件信息
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileUrl(FilePathConfig.switchUrl(pdfSavePath));
        fileDTO.setFileExtend("pdf");
        fileDTO.setFileSize(new File(pdfSavePath).length());
        fileDTO.setFileName(pdfSavePath.substring(pdfSavePath.lastIndexOf("/") + 1));
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
