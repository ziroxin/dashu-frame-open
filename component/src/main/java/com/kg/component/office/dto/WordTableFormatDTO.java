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
    public WordTableFormatDTO() {
        toDefault();
    }

    // 表格宽度类型（PCT-百分比）
    private TableWidthType widthType;
    // 表格宽度
    private String width;
    // 表格对齐方式（该表格在行内的对齐方式，不是表格内容的对齐方式）
    private TableRowAlign align;
    // 行高
    private int rowHeight;
    // 单元格对齐方式
    private ParagraphAlignment cellAlign;
    // 单元格垂直对齐方式
    private XWPFTableCell.XWPFVertAlign cellValign;
    // 单元格段前空白
    private int cellSpaceBefore;
    // 单元格段后空白
    private int cellSpaceAfter;
    // 单元格背景色
    private String bgColor;

    /**
     * 默认格式
     */
    public void toDefault() {
        widthType = TableWidthType.PCT;// 宽度类型：百分比
        width = "100%";// 宽度：100%
        align = TableRowAlign.CENTER;// 对齐方式：居中
        rowHeight = 400;// 行高：400
        cellAlign = ParagraphAlignment.CENTER;// 单元格对齐方式：居中
        cellValign = XWPFTableCell.XWPFVertAlign.CENTER;// 单元格垂直对齐方式：居中
        cellSpaceBefore = 0;// 单元格段前空白：0
        cellSpaceAfter = 0;// 单元格段后空白：0
        bgColor = "FFFFFF";// 单元格背景色：白色
    }
}
