package com.kg.component.office;

import com.kg.component.office.dto.WordStrFormatDTO;
import com.kg.component.office.dto.WordTableFormatDTO;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.util.ArrayList;
import java.util.List;

/**
 * Word 表格 - 操作工具类
 *
 * @author ziro
 * @date 2024/1/23 13:30
 */
public class WordWriteTableUtils {

    /**
     * 匹配 ${key} 写入表格
     * 默认创建一个：宽度100%，居中显示的表格
     *
     * @param doc          所操作的文档
     * @param keyStr       要匹配的key
     * @param rowLength    行数
     * @param columnLength 列数
     * @return 返回已插入的表格
     */
    public static XWPFTable tableWriteByKey(XWPFDocument doc, String keyStr, int rowLength, int columnLength, boolean isAppend) {
        // 另起一行，并获取当前段落
        XWPFParagraph currentParagraph = WordWriteStringUtils.writeStrNewline(doc, keyStr, "", null, false);

        // 获取光标的位置
        XmlCursor cursor = currentParagraph.getCTP().newCursor();
        // 在光标位置创建表格（默认创建 1行1列的表）
        XWPFTable table = doc.insertNewTbl(cursor);
        // 创建表格行
        for (int i = 0; i < rowLength; i++) {
            XWPFTableRow row = i < table.getNumberOfRows() ? table.getRow(i) : table.createRow();
            // 创建表格列
            for (int j = 0; j < columnLength; j++) {
                if (j >= row.getTableCells().size()) {
                    row.createCell();
                }
            }
        }
        if (isAppend) {
            currentParagraph.createRun().setText("${" + keyStr + "}");
        }
        return table;
    }

    /**
     * table写入单行
     *
     * @param table    所操作的表格
     * @param startRow 开始写入行（从0开始计数）
     * @param rowData  一行数据
     * @param cover    是否覆盖已有数据
     */
    public static void tableWriteRowOne(XWPFTable table, int startRow, List<String> rowData, boolean cover) {
        tableWriteRowOne(table, startRow, rowData, cover, null);
    }

    /**
     * table写入单行（自定义格式）
     *
     * @param table    所操作的表格
     * @param startRow 开始写入行（从0开始计数）
     * @param rowData  一行数据
     * @param cover    是否覆盖已有数据
     */
    public static void tableWriteRowOne(XWPFTable table, int startRow, List<String> rowData, boolean cover,
                                        WordStrFormatDTO format) {
        List<List<String>> rowList = new ArrayList<>();
        rowList.add(rowData);
        tableWriteRowList(table, startRow, rowList, cover, format);
    }

    /**
     * table写入多行
     *
     * @param table    所操作的表格
     * @param startRow 开始写入行（从0开始计数）
     * @param rowList  写入的数据
     * @param cover    是否覆盖已有数据
     */
    public static void tableWriteRowList(XWPFTable table, int startRow, List<List<String>> rowList, boolean cover) {
        tableWriteRowList(table, startRow, rowList, cover, null);
    }


    /**
     * table写入多行（自定义格式）
     *
     * @param table    所操作的表格
     * @param startRow 开始写入行（从0开始计数）
     * @param rowList  写入的数据
     * @param cover    是否覆盖已有数据
     */
    public static void tableWriteRowList(XWPFTable table, int startRow, List<List<String>> rowList, boolean cover,
                                         WordStrFormatDTO format) {
        int rowIndex = startRow < 0 ? 0 : startRow;
        for (List<String> cellData : rowList) {
            // 取行
            XWPFTableRow row = rowIndex < table.getNumberOfRows() ? table.getRow(rowIndex) : table.createRow();
            for (int i = 0; i < cellData.size(); i++) {
                if (cellData.get(i) == null) {
                    continue;// 跳过null数据
                }
                // 取列
                XWPFTableCell cell = i < row.getTableCells().size() ? row.getCell(i) : row.createCell();
                // 覆盖，先删除原内容
                if (cover) {
                    for (int j = cell.getParagraphs().size() - 1; j >= 0; j--) {
                        cell.removeParagraph(j);
                    }
                }
                // 写入数据
                XWPFRun run = cell.getParagraphs().size() > 0 ?
                        cell.getParagraphs().get(0).createRun() : cell.addParagraph().createRun();
                run.setText(cellData.get(i));
                if (format != null) {
                    // 设置样式
                    WordWriteStringUtils.runTextFormat(run, format);
                }
            }
            rowIndex++;
        }
    }

    /**
     * 合并单元格
     */
    public static void tableMerge(XWPFTable table, int startRow, int endRow, int startCol, int endCol) {
        for (int i = startRow; i <= endRow; i++) {
            XWPFTableRow row = table.getRow(i);
            for (int j = startCol; j <= endCol; j++) {
                if (endRow > startRow) {
                    if (i == startRow) {
                        row.getCell(j).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
                    } else {
                        row.getCell(j).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
                    }
                }
                if (endCol > startCol) {
                    if (j == startCol) {
                        row.getCell(j).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                    } else {
                        row.getCell(j).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                    }
                }
            }
        }
    }

    /**
     * 设置表格整体格式
     */
    public static void tableFormat(XWPFTable table, WordTableFormatDTO format) {
        // 1 设置表宽度
        table.setWidthType(format.getWidthType());
        table.setWidth(format.getWidth());
        // 2 表格居中（不是内容居中）
        table.setTableAlignment(TableRowAlign.RIGHT);
        // 表格内容居中
        table.getRows().stream().forEach(r -> {
            // 3 行高
            r.setHeight(format.getRowHeight());
            r.getTableCells().stream().forEach(c -> {
                c.getParagraphs().forEach(p -> {
                    // 4 内容水平居中
                    p.setAlignment(format.getCellAlign());
                    // 5 单倍行距
                    p.setSpacingBefore(format.getCellSpaceBefore());// 段前
                    p.setSpacingAfter(format.getCellSpaceAfter());// 段后
                });
                // 6 单元格垂直居中
                c.setVerticalAlignment(format.getCellValign());
                // 7 单元格背景色
                c.setColor(format.getBgColor());
            });
        });
    }

    /**
     * 设置单元格格式
     */
    public static void tableCellFormat(XWPFTable table, int startRow, int endRow, int startCol, int endCol,
                                    WordTableFormatDTO format) {
        for (int i = startRow; i <= endRow; i++) {
            XWPFTableRow row = table.getRow(i);
            // 3 行高
            row.setHeight(format.getRowHeight());
            for (int j = startCol; j <= endCol; j++) {
                XWPFTableCell c = row.getCell(j);
                c.getParagraphs().forEach(p -> {
                    // 4 内容水平居中
                    p.setAlignment(format.getCellAlign());
                    // 5 单倍行距
                    p.setSpacingBefore(format.getCellSpaceBefore());// 段前
                    p.setSpacingAfter(format.getCellSpaceAfter());// 段后
                });
                // 6 单元格垂直居中
                c.setVerticalAlignment(format.getCellValign());
                // 7 单元格背景色
                c.setColor(format.getBgColor());
            }
        }
    }
}
