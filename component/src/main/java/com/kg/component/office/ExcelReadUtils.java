package com.kg.component.office;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel 导入读取 - 工具类
 *
 * @author ziro
 * @date 2022-12-27 15:06:45
 */
public class ExcelReadUtils {
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
            MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = req.getFile(req.getFileMap().entrySet().stream().findFirst().get().getKey());
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

    /**
     * 读取Excel（List<Map>） - 导入Excel
     *
     * @param request        请求信息
     * @param headerRowIndex 标题所在行（从0开始计数）
     * @param startRowIndex  数据起始行（包含，从0开始计数）
     * @param alias          字段-标题 对应关系，例如：{"name":"姓名","age":"年龄","字段名":"标题字段名"}
     * @return 读取出的实体列表
     */
    public static List<Map<String, Object>> read(HttpServletRequest request, int headerRowIndex, int startRowIndex,
                                                 Map<String, String> alias) {
        try {
            MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = req.getFile(req.getFileMap().entrySet().stream().findFirst().get().getKey());
            InputStream inputStream = multipartFile.getInputStream();
            //读取文件为ExcelReader对象
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            //设置标题别名
            if (alias != null) {
                reader.setHeaderAlias(alias);
            }
            List<Map<String, Object>> dataList = reader.read(headerRowIndex, startRowIndex, Integer.MAX_VALUE);
            //返回集合
            return dataList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}