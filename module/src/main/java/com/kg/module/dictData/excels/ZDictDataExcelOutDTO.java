package com.kg.module.dictData.excels;

import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 字典数据
 *
 * @author ziro
 * @date 2023-05-26
 */
@Getter
@Setter
public class ZDictDataExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 字典数据ID
     */
//    private String dictId;

    /**
     * 所属类型code
     */
//    private String typeCode;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 状态0停用1正常
     */
    private String status;

    /**
     * 顺序
     */
    private Integer orderIndex;

    /**
     * 创建时间
     */
//    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
//    private LocalDateTime createTime;

    /**
     * 修改时间
     */
//    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
//    private LocalDateTime updateTime;

}