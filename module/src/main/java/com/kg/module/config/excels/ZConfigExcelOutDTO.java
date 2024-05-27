package com.kg.module.config.excels;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 参数参数配置
 *
 * @author ziro
 * @date 2024-05-27
 */
@Getter
@Setter
public class ZConfigExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 系统参数ID
     */
    private String cfgId;

    /**
     * 参数名称
     */
    private String cfgName;

    /**
     * 参数键名
     */
    private String cfgKey;

    /**
     * 参数键值
     */
    private String cfgValue;

    /**
     * 备注
     */
    private String cfgRemark;

    /**
     * 顺序
     */
    private Integer orderIndex;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}