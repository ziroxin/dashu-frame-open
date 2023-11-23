package com.kg.module.generator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 代码生成器表单
 * </p>
 *
 * @author ziro
 * @since 2023-11-22
 */
@Getter
@Setter
@TableName("z_form_generator")
@ApiModel(value = "ZFormGenerator对象", description = "代码生成器表单")
public class ZFormGenerator implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("表单id")
    @TableId(value = "form_id", type = IdType.ASSIGN_UUID)
    private String formId;

    @ApiModelProperty("表单名称")
    @TableField("form_name")
    private String formName;

    @ApiModelProperty("表单内容json格式")
    @TableField("form_json")
    private String formJson;

    @ApiModelProperty("表名")
    @TableField("table_name")
    private String tableName;

    @ApiModelProperty("表描述")
    @TableField("table_decription")
    private String tableDecription;

    @ApiModelProperty("pom模块名")
    @TableField("base_package")
    private String basePackage;

    @ApiModelProperty("作者")
    @TableField("author")
    private String author;

    @ApiModelProperty("生成包名")
    @TableField("table_package")
    private String tablePackage;

    @ApiModelProperty("前端路径")
    @TableField("view_path")
    private String viewPath;

    @ApiModelProperty("代码生成状态（0未生成1已生成）")
    @TableField("status")
    private String status;

    @ApiModelProperty("显示顺序")
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
