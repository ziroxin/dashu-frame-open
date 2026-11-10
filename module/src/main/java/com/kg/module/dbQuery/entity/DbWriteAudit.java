package com.kg.module.dbQuery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 数据库写操作审计
 *
 * @author ziro
 * @date 2026/3/9
 */
@Getter
@Setter
@TableName("z_db_write_audit")
@ApiModel(value = "DbWriteAudit对象", description = "数据库写操作审计")
public class DbWriteAudit implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("审计ID")
    @TableId(value = "audit_id", type = IdType.ASSIGN_UUID)
    private String auditId;

    @ApiModelProperty("操作用户ID")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("操作用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty("目标数据库")
    @TableField("database_name")
    private String databaseName;

    @ApiModelProperty("语句类型：INSERT/UPDATE/DELETE")
    @TableField("sql_type")
    private String sqlType;

    @ApiModelProperty("执行SQL")
    @TableField("sql_text")
    private String sqlText;

    @ApiModelProperty("预览影响行数")
    @TableField("preview_rows")
    private Integer previewRows;

    @ApiModelProperty("实际影响行数")
    @TableField("affected_rows")
    private Integer affectedRows;

    @ApiModelProperty("是否成功")
    @TableField("success")
    private Boolean success;

    @ApiModelProperty("结果说明/错误信息")
    @TableField("message")
    private String message;

    @ApiModelProperty("快照token")
    @TableField("snapshot_token")
    private String snapshotToken;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
