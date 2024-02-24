package com.kg.component.office;

import com.kg.component.office.dto.WordStrFormatDTO;
import com.kg.component.office.dto.WordTableFormatDTO;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word常用操作 - 工具类
 *
 * @author ziro
 * @date 2024/1/23 13:30
 */
public class WordCommonUtils {

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
        XWPFParagraph currentParagraph = writeStrNewline(doc, keyStr, "", null, false);

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
    public static void tableWriteRowOne(XWPFTable table, int startRow, List<String> rowData, boolean cover, WordStrFormatDTO format) {
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
    public static void tableWriteRowList(XWPFTable table, int startRow, List<List<String>> rowList, boolean cover, WordStrFormatDTO format) {
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
                    runTextFormat(run, format);
                }
            }
            rowIndex++;
        }
    }

    /**
     * 合并单元格
     */
    public static void tableMerge(XWPFTable table, int startRow, int endRow, int startCol, int endCol) throws Exception {
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
     * 格式化表格
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
     * 设置单元格背景色
     */
    public static void tableBgColor(XWPFTable table, int startRow, int endRow, int startCol, int endCol,
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


    /**
     * 匹配 ${key} 写入文本内容
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrByKey(XWPFDocument doc, String key, String content, boolean isAppend) {
        return writeStrByKey(doc, key, content, null, isAppend);
    }

    /**
     * 匹配 ${key} 写入文本内容（自定义格式）
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @param format  常用格式设置
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrByKey(XWPFDocument doc, String key, String content,
                                              WordStrFormatDTO format, boolean isAppend) {
        key = "${" + key + "}";
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            StringBuilder sb = new StringBuilder();
            for (XWPFRun run : paragraph.getRuns()) {
                sb.append(run.getText(0));// 本段落内容关联起来
            }
            // 匹配key
            String text = sb.toString();
            Pattern pattern = Pattern.compile(Pattern.quote(key));
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // 移除文档中包含${key}的内容
                for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {
                    paragraph.removeRun(i);
                }
                // 插入替换后的内容
                XWPFRun run = paragraph.getRuns().get(0);
                text = matcher.replaceAll(content);// 替换段落中${key}内容
                run.setText(text, 0);
                if (format != null) {
                    // 设置样式
                    runTextFormat(run, format);
                }
                if (isAppend) {
                    // 追加内容，把原标签写入
                    paragraph.createRun().setText(key);
                }
                return paragraph;
            }
        }
        return null;
    }

    /**
     * 另起一行：匹配 ${key} 写入文本内容
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrNewline(XWPFDocument doc, String key, String content, boolean isAppend) {
        return writeStrNewline(doc, key, content, null, isAppend);
    }


    /**
     * 另起一行：匹配 ${key} 写入文本内容（自定义格式）
     *
     * @param doc     所操作文档
     * @param key     要匹配的key
     * @param content 写入文本内容
     * @param format  常用格式设置
     * @return 返回当前段落
     */
    public static XWPFParagraph writeStrNewline(XWPFDocument doc, String key, String content,
                                                WordStrFormatDTO format, boolean isAppend) {
        key = "${" + key + "}";
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            StringBuilder sb = new StringBuilder();
            for (XWPFRun run : paragraph.getRuns()) {
                sb.append(run.getText(0));// 本段落内容关联起来
            }
            // 匹配key
            String text = sb.toString();
            Pattern pattern = Pattern.compile(Pattern.quote(key));
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // 移除文档中包含${key}的内容
                for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {
                    paragraph.removeRun(i);
                }
                // 插入替换后的内容
                XWPFRun run = paragraph.getRuns().get(0);
                run.setText(matcher.replaceAll(""), 0);// 先把原内容写入，替换掉${key}
                if (StringUtils.hasText(run.text())) {
                    // 创建新段落，写入新内容
                    XmlCursor cursor1 = paragraph.getCTP().newCursor();
                    cursor1.toNextSibling();
                    XWPFParagraph newParagraph = doc.insertNewParagraph(cursor1);
                    XWPFRun newRun = newParagraph.createRun();
                    newRun.setText(content, 0);
                    paragraph = newParagraph;
                    run = newRun;
                } else {
                    // 原内容为空，则直接写入
                    run.setText(content, 0);
                }
                if (format != null) {
                    // 设置样式
                    runTextFormat(run, format);
                }
                if (isAppend) {
                    // 追加内容，在新段落把原标签写入
                    XmlCursor cursor2 = paragraph.getCTP().newCursor();
                    cursor2.toNextSibling();
                    doc.insertNewParagraph(cursor2).createRun().setText(key);
                }
                return paragraph;
            }
        }
        return null;
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
     * 获取格式
     */
    private static WordStrFormatDTO getFormat(XWPFRun run) {
        WordStrFormatDTO format = new WordStrFormatDTO();
        format.setBold(run.isBold());
        format.setItalic(run.isItalic());
        format.setUnderline(run.getUnderline() == UnderlinePatterns.SINGLE);
        format.setFontFamily(run.getFontFamily());
        if (run.getFontSize() > 0) {
            format.setFontSize(run.getFontSize());
        }
        format.setColor(run.getColor());
        return format;
    }
}
