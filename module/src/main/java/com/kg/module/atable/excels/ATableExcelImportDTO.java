package com.kg.module.atable.excels;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导入Excel实体 - 我的表a_table
 *
 * @author ziro
 * @date 2025-03-18
 */
@Getter
@Setter
public class ATableExcelImportDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    /** 手机号 */
    private String mobile;

    /** 顺序 */
    private String orderIndex;

    /** 测试单行文本 */
    private String testText;

    /** 富文本框 */
    private String testEditor;

    /** 测试decimal */
    private String testDecimal;

    /** ImageOne */
    private String testImg;

    /** ImageAvatar */
    private String testAvatar;
}