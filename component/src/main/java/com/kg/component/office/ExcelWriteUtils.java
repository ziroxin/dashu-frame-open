package com.kg.component.office;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Excel 导出写入 - 工具类
 *
 * @author ziro
 * @date 2022-12-27 15:06:45
 */
public class ExcelWriteUtils {
    /**
     * 导出excel文件（简单表头）
     *
     * @param path      文件生成路径
     * @param title     第一行标题（大标题）
     * @param dataList  数据集合（实体类）
     * @param columnMap 字段-标题 对应关系（按顺序导出），例如：{"name":"姓名","age":"年龄","字段名":"标题字段名"}
     * @return 是否导出成功
     */
    public static boolean write(String path, String title, List<?> dataList, LinkedHashMap<String, String> columnMap) {
        try {
            // 写入准备
            ExcelWriter writer = ExcelWriteUtils.writePrepare(path, title, columnMap);
            // 写入内容
            writer.write(dataList, true);
            if (writer != null) {
                writer.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成导入模板
     *
     * @param path      文件生成路径
     * @param title     第一行标题（大标题）
     * @param columnMap 字段-标题
     * @return 是否生成成功
     */
    public static boolean writeTemplate(String path, String title, LinkedHashMap<String, String> columnMap) {
        try {
            // 写入准备
            ExcelWriter writer = ExcelWriteUtils.writePrepare(path, title, columnMap);
            // 写入标题行
            writer.writeHeadRow(columnMap.keySet());
            if (writer != null) {
                writer.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 导出excel文件：写入准备
     *
     * @param path      文件生成路径
     * @param title     第一行标题（大标题）
     * @param columnMap 字段-标题 对应关系（按顺序导出），例如：{"name":"姓名","age":"年龄","字段名":"标题字段名"}
     * @return 待写入的ExcelWriter对象
     */
    public static ExcelWriter writePrepare(String path, String title, LinkedHashMap<String, String> columnMap) {
        ExcelWriter writer = null;
        try {
            // 创建目标文件对象
            File file = new File(path);
            // 判断目标是否存在
            if (file.exists()) {
                file.delete();
            }
            // 创建writer
            writer = ExcelUtil.getWriter(path);
            // 设置标题行样式
            CellStyle cellStyle = writer.getStyleSet().getHeadCellStyle();
            Font font = writer.createFont();
            font.setBold(true);
            font.setFontHeight((short) 300);
            font.setFontName("宋体");
            cellStyle.setFont(font);// 字体
            // 设置整体行高
            writer.setDefaultRowHeight(25);
            // 设置整体列宽
            writer.setColumnWidth(-1, 30);

            // 写入第一行标题（合并单元格）
            writer.merge(columnMap.size() - 1, title);
            // 设置字段名
            writer.setHeaderAlias(columnMap);
            return writer;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}