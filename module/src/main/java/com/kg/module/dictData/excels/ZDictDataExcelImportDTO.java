package com.kg.module.dictData.excels;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导入Excel实体 - 字典数据
 *
 * @author ziro
 * @date 2023-05-26
 */
@Getter
@Setter
public class ZDictDataExcelImportDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 顺序
     */
    private String orderIndex;

}