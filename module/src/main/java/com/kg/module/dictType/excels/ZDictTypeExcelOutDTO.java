package com.kg.module.dictType.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 字典类型
 *
 * @author ziro
 * @date 2023-05-26
 */
@Getter
@Setter
public class ZDictTypeExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 字典类型ID
     */
    private String typeId;

    /**
     * 字典名称
     */
    private String typeName;

    /**
     * 字典类型code
     */
    private String typeCode;

    /**
     * 状态0停用1正常
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}