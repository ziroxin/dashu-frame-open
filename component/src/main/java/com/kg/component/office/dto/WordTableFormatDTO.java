package com.kg.component.office.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

/**
 * Word 表格 - 常用属性
 *
 * @author ziro
 * @date 2024/1/24 9:46
 */
@Getter
@Setter
public class WordTableFormatDTO {
    // 表格宽度类型（PCT-百分比）
    private TableWidthType widthType = TableWidthType.PCT;
    // 表格宽度
    private String width = "100%";
    // 表格对齐方式（该表格在行内的对齐方式，不是表格内容的对齐方式）
    private TableRowAlign align = TableRowAlign.CENTER;
    // 行高
    private int rowHeight = 400;
    // 单元格对齐方式
    private ParagraphAlignment cellAlign = ParagraphAlignment.CENTER;
    // 单元格垂直对齐方式
    private XWPFTableCell.XWPFVertAlign cellValign = XWPFTableCell.XWPFVertAlign.CENTER;
    // 单元格段前空白
    private int cellSpaceBefore = 0;
    // 单元格段后空白
    private int cellSpaceAfter = 0;

}
