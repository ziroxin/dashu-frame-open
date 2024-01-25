package com.kg;

import cn.hutool.json.JSONUtil;
import com.kg.component.office.WordCommonUtils;
import com.kg.component.office.dto.WordStrFormatDTO;
import com.kg.component.office.dto.WordTableFormatDTO;
import com.kg.module.news.mapper.NewsMapper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ziro
 * @date 2023/9/27 15:19
 */
@SpringBootTest
public class Test2 {
    @Resource
    private NewsMapper mapper;

    @Test
    public void test() {
        System.out.println("xml 方式");
        System.out.println(JSONUtil.toJsonStr(mapper.getByIdTestXml("4585d26d-1201-400f-8ab3-6e7d65310ee8")));

        System.out.println("mybatis plus 方式");
        System.out.println(JSONUtil.toJsonStr(mapper.selectById("4585d26d-1201-400f-8ab3-6e7d65310ee8")));

        System.out.println("注解方式");
        System.out.println(JSONUtil.toJsonStr(mapper.getByIdTestSQL("4585d26d-1201-400f-8ab3-6e7d65310ee8")));
    }

    public static void main(String[] args) {
        try {
            String path = System.getProperty("user.dir") + "/module/target/hello.docx";
            String outPath = System.getProperty("user.dir") + "/module/target/hello1.docx";
            XWPFDocument doc = new XWPFDocument(new FileInputStream(path));

            // 1 写入内容
            WordCommonUtils.writeStrByKey(doc, "data1", "标题1");
            WordCommonUtils.writeStrByKey(doc, "data2", "标题2");
            WordCommonUtils.writeStrByKey(doc, "data4", "内容4");
            WordCommonUtils.writeStrByKey(doc, "data5", "内容5");

            // 2 写入表格
            XWPFTable table = WordCommonUtils.tableWriteByKey(doc, "data3", 10, 4);
            // 2.1 标题行（自定义格式）
            WordStrFormatDTO titleFormat = new WordStrFormatDTO();
            titleFormat.setBold(true);
            titleFormat.setFontSize(14);
            String[] titles = new String[]{"类型", "总受理量（件）", "已处理数量（件）", "未处理数量（件）"};
            WordCommonUtils.tableWriteRowOne(table, 0, Arrays.asList(titles), true, titleFormat);

            // 2.2 数据行
            List<List<String>> rowList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                List<String> row = new ArrayList<>();
                row.add("类型" + i);
                row.add("总受理量" + 1000);
                row.add("已处理数量" + (700 + i));
                row.add("未处理数量" + (300 - i));
                rowList.add(row);
            }
            WordCommonUtils.tableWriteRowList(table, 1, rowList, true);

            // 2.3 合并单元格
            String[] other = new String[]{"合计"};
            WordCommonUtils.tableWriteRowOne(table, 6, Arrays.asList(other), true);
            WordCommonUtils.tableMerge(table, 6, 7, 0, 0);

            String[] other2 = new String[]{"合计2"};
            WordCommonUtils.tableWriteRowOne(table, 8, Arrays.asList(other2), true);
            WordCommonUtils.tableMerge(table, 8, 8, 0, 2);

            // 写入某一单元格（第3行 第3列）
            String[] other3 = new String[]{null, null, "只修改第3行第3列"};
            WordCommonUtils.tableWriteRowOne(table, 2, Arrays.asList(other3), true);

            // 2.4 格式化表格（内容居中）
            WordCommonUtils.tableFormat(table, new WordTableFormatDTO());

            // 2.5 给某 n 个单元格配置格式
            WordTableFormatDTO cellFormat = new WordTableFormatDTO();
            cellFormat.setBgColor("CCCCCC");// 设置标题行背景色和行高
            cellFormat.setRowHeight(800);
            WordCommonUtils.tableBgColor(table, 0, 0,
                    0, table.getRow(0).getTableCells().size() - 1, cellFormat);
            cellFormat.toDefault();// 恢复默认配置
            cellFormat.setBgColor("FF0000");
            WordCommonUtils.tableBgColor(table, 3, 4, 1, 2, cellFormat);


            doc.write(new FileOutputStream(outPath));
            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
