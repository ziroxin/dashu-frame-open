package com.kg.component.office;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel常用操作 - 工具类
 *
 * @author ziro
 * @date 2022-12-27 15:06:45
 */
public class ExcelCommonUtils {

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
            // 写入内容
            writer.write(dataList, true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取Excel - 导入Excel
     *
     * @param request        请求信息
     * @param headerRowIndex 标题所在行（从0开始计数）
     * @param startRowIndex  数据起始行（包含，从0开始计数）
     * @param clazz          返回值数据集合（实体类）
     * @param alias          字段-标题 对应关系，例如：{"name":"姓名","age":"年龄","字段名":"标题字段名"}
     * @return 读取出的实体列表
     */
    public static <T> List<T> read(HttpServletRequest request, int headerRowIndex, int startRowIndex,
                                   Class clazz, Map<String, String> alias) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartRequest.getFile(multipartRequest.getFileMap().entrySet().stream().findFirst().get().getKey());
            InputStream inputStream = multipartFile.getInputStream();
            //读取文件为ExcelReader对象
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            //设置标题别名
            if (alias != null) {
                reader.setHeaderAlias(alias);
            }
            List<T> dataList = reader.read(headerRowIndex, startRowIndex, clazz);
            //返回集合
            return dataList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}