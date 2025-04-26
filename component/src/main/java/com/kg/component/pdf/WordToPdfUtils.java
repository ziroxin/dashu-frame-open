package com.kg.component.pdf;

import cn.hutool.core.io.FileUtil;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import org.springframework.util.StringUtils;

import java.io.File;


/**
 * Word转PDF工具类
 *
 * @author ziro
 * @date 2024/5/28 15:55
 */
public class WordToPdfUtils {

    /**
     * word转pdf
     *
     * @param fileUrl word文件路径
     * @return pdf文件DTO
     */
    public static FileDTO toPdf(String fileUrl) throws Exception {
        return toPdf(fileUrl, null);
    }

    /**
     * word转pdf
     *
     * @param fileUrl   word文件路径
     * @param outFolder 输出文件夹（为空时默认与word文件同目录）
     * @return pdf文件DTO
     */
    public static FileDTO toPdf(String fileUrl, String outFolder) throws Exception {
        // 读取word
        String savePath = FilePathConfig.switchSavePath(fileUrl);
        Document document = new Document(savePath);
        // 保存pdf
        String docxName = savePath.substring(savePath.lastIndexOf("/"));
        String pdfSavePath = StringUtils.hasText(outFolder)
                ? FilePathConfig.SAVE_PATH + "/" + outFolder + "/" + docxName.substring(0, docxName.lastIndexOf(".")) + ".pdf"
                : savePath.substring(0, savePath.lastIndexOf(".")) + ".pdf";
        pdfSavePath = pdfSavePath.replaceAll("//", "/");
        FileUtil.mkParentDirs(pdfSavePath);
        document.save(pdfSavePath, SaveFormat.PDF);
        FileDTO fileDTO = new FileDTO();
        // 切换url
        fileDTO.setFileUrl(FilePathConfig.switchUrl(pdfSavePath));
        // 文件扩展名
        fileDTO.setFileExtend("pdf");
        // 计算文件大小
        fileDTO.setFileSize(new File(pdfSavePath).length());
        // 文件名
        fileDTO.setFileName(pdfSavePath.substring(pdfSavePath.lastIndexOf("/") + 1));
        return fileDTO;
    }
}
