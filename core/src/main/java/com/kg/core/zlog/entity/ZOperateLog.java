package com.kg.core.zlog.entity;

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
 * 操作日志表
 * </p>
 *
 * @author ziro
 * @since 2023-01-07
 */
@Getter
@Setter
@TableName("z_operate_log")
@ApiModel(value = "ZOperateLog对象", description = "操作日志表")
public class ZOperateLog implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("操作日志id")
    @TableId(value = "log_id", type = IdType.ASSIGN_UUID)
    private String logId;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("操作人用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("执行方法名称")
    @TableField("log_method")
    private String logMethod;

    @ApiModelProperty("执行方法描述")
    @TableField("log_msg")
    private String logMsg;

    @ApiModelProperty("操作内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("请求路径")
    @TableField("action_url")
    private String actionUrl;

    @ApiModelProperty("IP地址")
    @TableField("ip")
    private String ip;

    @ApiModelProperty("执行结果（1成功 2失败）")
    @TableField("status")
    private Byte status;

    @ApiModelProperty("操作时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
