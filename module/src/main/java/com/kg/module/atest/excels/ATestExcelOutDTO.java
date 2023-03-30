package com.kg.module.atest.excels;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 测试表
 *
 * @author ziro
 * @date 2023-03-30
 */
@Getter
@Setter
public class ATestExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 测试id
     */
    private String testId;

    /**
     * 姓名
     */
    private String testName;

    /**
     * 年龄
     */
    private Integer testAge;

    /**
     * 性别
     */
    private String testSex;

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