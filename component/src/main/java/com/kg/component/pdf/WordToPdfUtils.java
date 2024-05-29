package com.kg.component.pdf;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;

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
     * @param filePath word文件路径
     * @return pdf文件DTO
     * @throws Exception word转pdf异常
     */
    public static FileDTO toPdf(String filePath) throws Exception {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileUrl(filePath);
        return toPdf(fileDTO);
    }

    /**
     * word转pdf
     *
     * @param fileDTO word文件DTO
     * @return pdf文件DTO
     * @throws Exception word转pdf异常
     */
    public static FileDTO toPdf(FileDTO fileDTO) throws Exception {
        // 读取word
        String savePath = FilePathConfig.switchSavePath(fileDTO.getFileUrl());
        Document document = new Document(savePath);
        // 保存pdf
        String pdfSavePath = savePath.substring(0, savePath.lastIndexOf(".")) + ".pdf";
        document.save(pdfSavePath, SaveFormat.PDF);
        // 切换url
        fileDTO.setFileUrl(FilePathConfig.switchUrl(pdfSavePath));
        // 文件扩展名
        fileDTO.setFileExtend("pdf");
        // 计算文件大小
        fileDTO.setFileSize(new File(pdfSavePath).length());
        // 文件名
        fileDTO.setFileName(pdfSavePath.substring(pdfSavePath.lastIndexOf(File.separator) + 1));
        return fileDTO;
    }
}
