package com.kg.component.office.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Word 文本、段落 - 常用属性
 *
 * @author ziro
 * @date 2024/1/23 13:34
 */
@Getter
@Setter
public class WordStrFormatDTO {
    // 是否加粗
    private Boolean bold;

    // 是否斜体
    private Boolean italic;

    // 是否下划线
    private Boolean underline;

    // 字体（例如：仿宋  宋体  楷体  Microsoft YaHei）
    private String fontFamily;

    // 字号
    // 例如：10pt-五号 12pt-小四 14pt-四号 16pt-小三 18pt-三号 22pt-小二 24pt-二号 26pt-小一 36pt-一号
    private Integer fontSize;

    // 颜色（RRGGBB，如:FF0000表示红色）
    private String color;
}
