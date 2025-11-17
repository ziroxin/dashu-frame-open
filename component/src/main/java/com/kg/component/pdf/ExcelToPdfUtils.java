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
        // 读取excel文件路径
        String savePath = FilePathConfig.switchSavePath(fileUrl);
        // 创建Workbook对象
        Workbook workbook = null;
        try {
            workbook = new Workbook(savePath);
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
            // 确定PDF保存路径
            String xlsxName = new File(savePath).getName();
            String pdfSavePath = StringUtils.hasText(outFolder)
                    ? FilePathConfig.SAVE_PATH + "/" + outFolder + "/" + xlsxName.substring(0, xlsxName.lastIndexOf(".")) + ".pdf"
                    : savePath.substring(0, savePath.lastIndexOf(".")) + ".pdf";
            pdfSavePath = pdfSavePath.replaceAll("//", "/");
            // 确保输出文件夹存在
            FileUtil.mkParentDirs(pdfSavePath);
            // 设置PDF保存选项
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);
            // 保存PDF文件
            workbook.save(pdfSavePath, pdfSaveOptions);
            // 创建FileDTO对象
            FileDTO fileDTO = new FileDTO();
            // 设置文件URL
            fileDTO.setFileUrl(FilePathConfig.switchUrl(pdfSavePath));
            // 设置文件扩展名
            fileDTO.setFileExtend("pdf");
            // 计算文件大小
            fileDTO.setFileSize(new File(pdfSavePath).length());
            // 设置文件名
            fileDTO.setFileName(new File(pdfSavePath).getName());
            return fileDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Excel转PDF失败：" + e.getMessage());
        } finally {
            // 关闭Workbook对象
            workbook.dispose();
        }
    }
}
