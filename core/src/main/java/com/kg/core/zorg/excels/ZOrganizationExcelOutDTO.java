package com.kg.core.zorg.excels;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 导出Excel实体 - 组织机构表
 *
 * @author ziro
 * @date 2023-01-11
 */
@Getter
@Setter
public class ZOrganizationExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 组织机构ID
     */
    private String orgId;

    /**
     * 组织机构名称
     */
    private String orgName;

    /**
     * 父级ID
     */
    private String orgParentId;

    /**
     * 组织机构路径(格式:id1.id2.id3)
     */
    private String orgPath;

    /**
     * 层级
     */
    private Integer orgLevel;

    /**
     * 备注
     */
    private Integer remarks;

    /**
     * 顺序
     */
    private Integer orderIndex;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}