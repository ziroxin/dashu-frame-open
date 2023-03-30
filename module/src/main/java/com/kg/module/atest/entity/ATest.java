package com.kg.module.atest.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 测试表
 * </p>
 *
 * @author ziro
 * @since 2023-03-30
 */
@Getter
@Setter
@TableName("a_test")
@ApiModel(value = "ATest对象", description = "测试表")
public class ATest implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("测试id")
    @TableId(value = "test_id", type = IdType.ASSIGN_UUID)
    private String testId;

    @ApiModelProperty("姓名")
    @TableField("test_name")
    private String testName;

    @ApiModelProperty("年龄")
    @TableField("test_age")
    private Integer testAge;

    @ApiModelProperty("性别")
    @TableField("test_sex")
    private String testSex;

    @ApiModelProperty("顺序")
    @TableField("order_index")
    private Integer orderIndex;

    @ApiModelProperty("添加时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
