package com.kg.core.zsafety.excels;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 导出Excel实体 - 密码安全等设置
 *
 * @author ziro
 * @date 2022-12-30
 */
@Getter
@Setter
public class ZSafetyExcelOutDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;


    /**
     * 配置ID
     */
    private Integer id;

    /**
     * 开始长度
     */
    private Short startLength;

    /**
     * 结束长度
     */
    private Short endLength;

    /**
     * 小写字母 0必须无 1必须有 2可有可无
     */
    private Short lowercase;

    /**
     * 大写字母 0必须无 1必须有 2可有可无
     */
    private Short uppercase;

    /**
     * 数字 0必须无 1必须有 2可有可无
     */
    private Short numbers;

    /**
     * 是否有特殊字符 0必须无 1必须有 2可有可无
     */
    private Short specialCharacters;

    /**
     * 不能包含用户名 0否 1是
     */
    private Short banUsername;

    /**
     * 有效时间 天
     */
    private Short validTime;

    /**
     * 提示语
     */
    private String prompt;

    /**
     * 登录失败限制次数
     */
    private Short loginFailedTimes;

    /**
     * 锁定时间 分钟
     */
    private Short lockTime;

    /**
     * 默认密码
     */
    private String defaultPassword;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}