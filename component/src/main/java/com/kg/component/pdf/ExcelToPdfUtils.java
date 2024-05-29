package com.kg.component.pdf;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;

import java.io.File;


/**
 * Excel转PDF工具类
 *
 * @author ziro
 * @date 2024/5/28 15:55
 */
public class ExcelToPdfUtils {
    /**
     * excel转pdf（全部工作簿）
     *
     * @param filePath excel文件路径
     * @return pdf文件DTO
     * @throws Exception excel转pdf异常
     */
    public static FileDTO toPdf(String filePath) throws Exception {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileUrl(filePath);
        return toPdf(fileDTO, -1);
    }

    /**
     * excel转pdf（指定工作簿）
     *
     * @param filePath   excel文件路径
     * @param sheetIndex sheet索引（从0开始，-1为全部工作簿）
     * @return pdf文件DTO
     * @throws Exception excel转pdf异常
     */
    public static FileDTO toPdf(String filePath, int sheetIndex) throws Exception {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileUrl(filePath);
        return toPdf(fileDTO, sheetIndex);
    }

    /**
     * excel转pdf（全部工作簿）
     *
     * @param fileDTO excel文件DTO
     * @return pdf文件DTO
     * @throws Exception excel转pdf异常
     */
    public static FileDTO toPdf(FileDTO fileDTO) throws Exception {
        return toPdf(fileDTO, -1);
    }

    /**
     * excel转pdf（指定工作簿）
     *
     * @param fileDTO    excel文件DTO
     * @param sheetIndex sheet索引（从0开始，-1为全部工作簿）
     * @return pdf文件DTO
     * @throws Exception excel转pdf异常
     */
    public static FileDTO toPdf(FileDTO fileDTO, int sheetIndex) throws Exception {
        // 读取excel
        String savePath = FilePathConfig.switchSavePath(fileDTO.getFileUrl());
        Workbook workbook = new Workbook(savePath);
        // 切换工作簿
        if (sheetIndex >= 0) {
            WorksheetCollection sheets = workbook.getWorksheets();
            if (sheetIndex < sheets.getCount()) {
                for (int i = 0; i < sheets.getCount(); i++) {
                    if (i != sheetIndex) sheets.get(i).setVisible(false);
                }
            }
        }
        // 保存pdf
        String pdfSavePath = savePath.substring(0, savePath.lastIndexOf(".")) + ".pdf";
        PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
        pdfSaveOptions.setOnePagePerSheet(true);
        workbook.save(pdfSavePath, pdfSaveOptions);
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
