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

    /** 姓名 */
    private String testName;

    /** ImageAvatar */
    private String field118;

    /** ImageOne */
    private String field119;
}