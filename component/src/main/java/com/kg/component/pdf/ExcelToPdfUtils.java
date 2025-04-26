package com.kg.component.pdf;

import cn.hutool.core.io.FileUtil;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import org.springframework.util.StringUtils;

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
     * @param fileUrl excel文件路径
     * @return pdf文件DTO
     */
    public static FileDTO toPdf(String fileUrl) throws Exception {
        return toPdf(fileUrl, null, -1);
    }

    /**
     * excel转pdf（全部工作簿）
     *
     * @param fileUrl   excel文件路径
     * @param outFolder 输出文件夹（为空时默认与excel同目录）
     * @return pdf文件DTO
     */
    public static FileDTO toPdf(String fileUrl, String outFolder) throws Exception {
        return toPdf(fileUrl, outFolder, -1);
    }

    /**
     * excel转pdf（指定工作簿）
     *
     * @param fileUrl    excel文件路径
     * @param sheetIndex sheet索引（从0开始，-1为全部工作簿）
     * @return pdf文件DTO
     */
    public static FileDTO toPdf(String fileUrl, int sheetIndex) throws Exception {
        return toPdf(fileUrl, null, sheetIndex);
    }

    /**
     * excel转pdf
     *
     * @param fileUrl    excel文件路径
     * @param outFolder  输出文件夹（为空时默认与excel同目录）
     * @param sheetIndex sheet索引（从0开始，-1为全部工作簿）
     * @return pdf文件DTO
     */
    public static FileDTO toPdf(String fileUrl, String outFolder, int sheetIndex) throws Exception {
        // 读取excel
        String savePath = FilePathConfig.switchSavePath(fileUrl);
        Workbook workbook = new Workbook(savePath);
        // 切换工作簿
        if (sheetIndex >= 0) {
            WorksheetCollection sheets = workbook.getWorksheets();
            if (sheetIndex < sheets.getCount()) {
                for (int i = 0; i < sheets.getCount(); i++) {
                    if (i != sheetIndex) {
                        // 隐藏无需转pdf的工作簿
                        sheets.get(i).setVisible(false);
                    }
                }
            }
        }
        // 保存pdf
        String xlsxName = savePath.substring(savePath.lastIndexOf("/"));
        String pdfSavePath = StringUtils.hasText(outFolder)
                ? FilePathConfig.SAVE_PATH + "/" + outFolder + "/" + xlsxName.substring(0, xlsxName.lastIndexOf(".")) + ".pdf"
                : savePath.substring(0, savePath.lastIndexOf(".")) + ".pdf";
        pdfSavePath = pdfSavePath.replaceAll("//", "/");
        FileUtil.mkParentDirs(pdfSavePath);
        PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
        pdfSaveOptions.setOnePagePerSheet(true);
        workbook.save(pdfSavePath, pdfSaveOptions);
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
