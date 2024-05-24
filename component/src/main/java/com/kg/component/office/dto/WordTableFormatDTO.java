package com.kg.component.office.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xwpf.usermodel.*;

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
    // 表格边框属类型（无边框设置为：XWPFTable.XWPFBorderType.NONE）
    private XWPFTable.XWPFBorderType borderType;
    // 表格边框宽度（无边框时无效）
    private int borderSize;
    // 表格边框空隙（无边框时无效）
    private int borderSpace;
    // 表格边框颜色（无边框时无效）
    private String borderColor;
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
        borderType = XWPFTable.XWPFBorderType.SINGLE;// 边框类型：单线
        borderSize = 1;// 边框宽度：1
        borderSpace = 0;// 边框空隙：0
        borderColor = "000000";// 边框颜色：黑色
        rowHeight = 400;// 行高：400
        cellAlign = ParagraphAlignment.CENTER;// 单元格对齐方式：居中
        cellValign = XWPFTableCell.XWPFVertAlign.CENTER;// 单元格垂直对齐方式：居中
        cellSpaceBefore = 0;// 单元格段前空白：0
        cellSpaceAfter = 0;// 单元格段后空白：0
        bgColor = "FFFFFF";// 单元格背景色：白色
    }

    /**
     * 设置无边框
     */
    public void setNoneBorder() {
        borderType = XWPFTable.XWPFBorderType.NONE;
        borderSize = 0;
        borderSpace = 0;
        borderColor = null;
    }
}
