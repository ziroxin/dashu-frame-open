package com.kg;

import cn.hutool.json.JSONUtil;
import com.kg.component.office.WordWriteImageUtils;
import com.kg.component.office.WordWriteStringUtils;
import com.kg.component.office.WordWriteTableUtils;
import com.kg.component.office.dto.WordStrFormatDTO;
import com.kg.component.office.dto.WordTableFormatDTO;
import com.kg.module.news.mapper.NewsMapper;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
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
            if (false) {
                String path = System.getProperty("user.dir") + "/module/src/test/java/com/kg/bbda.docx";
                String outPath = System.getProperty("user.dir") + "/module/target/hello1.docx";
                XWPFDocument doc = new XWPFDocument(new FileInputStream(path));

                WordWriteStringUtils.writeStrByKey(doc, "data1", "标题1", true);
                WordWriteStringUtils.writeStrByKey(doc, "data1", "标题11", false);
                WordWriteStringUtils.writeStrByKey(doc, "data2", "标题2", false);
                // 写入图片（自动宽高）
                File imgFile0 = new File("D:\\Users\\Administrator\\Pictures\\9315e9f1612a950938cdda0491795a95.gif");
                XWPFParagraph newP0 = WordWriteImageUtils.imageInTableAutoSize(doc, "qmyjr", imgFile0, true);
                newP0.setAlignment(ParagraphAlignment.LEFT); //图片居左（默认）
                WordWriteImageUtils.imageInTable(doc, "qmyjr", imgFile0, 100, 100, false);

                doc.write(new FileOutputStream(outPath));
                doc.close();

            }

            if (true) {
                String path = System.getProperty("user.dir") + "/module/src/test/java/com/kg/hello.docx";
                String outPath = System.getProperty("user.dir") + "/module/target/hello1.docx";
                XWPFDocument doc = new XWPFDocument(new FileInputStream(path));

                // 写入图片（自动宽高）
                File imgFile = new File("D:\\Users\\Administrator\\Pictures\\9315e9f1612a950938cdda0491795a95.gif");
                XWPFParagraph newP1 = WordWriteImageUtils.imageWriteAutoSize(doc, "pic1", imgFile, true);
                newP1.setAlignment(ParagraphAlignment.LEFT); //图片居左（默认）
                // 写入图片（指定宽高）
                File imgFile2 = new File("D:\\Users\\Administrator\\Pictures\\java119\\3.jpg");
                XWPFParagraph newP2 = WordWriteImageUtils.imageWrite(doc, "pic1", imgFile2, 400, 300, true);
                newP2.setAlignment(ParagraphAlignment.CENTER); //图片居中

                // 1 写入内容
                WordWriteStringUtils.writeStrByKey(doc, "data1", "标题1", false);

                WordWriteStringUtils.writeStrByKey(doc, "data2", "标题2", true);
                WordWriteStringUtils.writeStrByKey(doc, "data2", "===========标题2", false);

                WordWriteStringUtils.writeStrByKey(doc, "data4", "内容4", false);
                WordStrFormatDTO ff = new WordStrFormatDTO();
                ff.setBold(true);
                ff.setFontSize(14);
                WordWriteStringUtils.writeStrNewline(doc, "data5", "新行写入===", ff, true);
                WordWriteStringUtils.writeStrNewline(doc, "data5", "内容5新行写入===", true);
                WordWriteStringUtils.writeStrNewline(doc, "data5", "内容5新行写入===", true);
                WordWriteStringUtils.writeStrByKey(doc, "data5", "内容5", true);
                WordWriteStringUtils.writeStrByKey(doc, "data5", "内容5", true);

                // 2 写入表格
                XWPFTable table = WordWriteTableUtils.tableWriteByKey(doc, "data3", 10, 4, true);
                // 2.1 标题行（自定义格式）
                WordStrFormatDTO titleFormat = new WordStrFormatDTO();
                titleFormat.setBold(true);
                titleFormat.setFontSize(14);
                String[] titles = new String[]{"类型", "总受理量（件）", "已处理数量（件）", "未处理数量（件）"};
                WordWriteTableUtils.tableWriteRowOne(table, 0, Arrays.asList(titles), true, titleFormat);

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
                WordWriteTableUtils.tableWriteRowList(table, 1, rowList, true);

                // 2.3 合并单元格
                String[] other = new String[]{"合计"};
                WordWriteTableUtils.tableWriteRowOne(table, 6, Arrays.asList(other), true);
                WordWriteTableUtils.tableMerge(table, 6, 7, 0, 0);

                String[] other2 = new String[]{"合计2"};
                WordWriteTableUtils.tableWriteRowOne(table, 8, Arrays.asList(other2), true);
                WordWriteTableUtils.tableMerge(table, 8, 8, 0, 2);

                // 写入某一单元格（第3行 第3列）
                String[] other3 = new String[]{null, null, "只修改第3行第3列"};
                WordWriteTableUtils.tableWriteRowOne(table, 2, Arrays.asList(other3), true);

                // 2.4 格式化表格（内容居中）
                WordWriteTableUtils.tableFormat(table, new WordTableFormatDTO());

                // 2.5 给某 n 个单元格配置格式
                WordTableFormatDTO cellFormat = new WordTableFormatDTO();
                cellFormat.setBgColor("CCCCCC");// 设置标题行背景色和行高
                cellFormat.setRowHeight(800);
                WordWriteTableUtils.tableCellFormat(table, 0, 0,
                        0, table.getRow(0).getTableCells().size() - 1, cellFormat);
                cellFormat.toDefault();// 恢复默认配置
                cellFormat.setBgColor("FF0000");
                cellFormat.setNoneBorder();
                WordWriteTableUtils.tableCellFormat(table, 3, 4, 1, 2, cellFormat);


                doc.write(new FileOutputStream(outPath));
                doc.close();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
