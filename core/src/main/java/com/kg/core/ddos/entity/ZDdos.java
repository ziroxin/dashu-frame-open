package com.kg.core.ddos.entity;

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
 * ddos用户请求记录
 * </p>
 *
 * @author ziro
 * @since 2024-07-15
 */
@Getter
@Setter
@TableName("z_ddos")
@ApiModel(value = "ZDdos对象", description = "ddos用户请求记录")
public class ZDdos implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("DDoS_id")
    @TableId(value = "ddos_id", type = IdType.ASSIGN_UUID)
    private String ddosId;

    @ApiModelProperty("用户ID")
    @TableField("user_ip")
    private String userIp;

    @ApiModelProperty("请求次数")
    @TableField("request_count")
    private Integer requestCount;

    @ApiModelProperty("限制配置（例如：5分钟内限制100次）")
    @TableField("limit_json")
    private String limitJson;

    @ApiModelProperty("用户id（有则保存）")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("记录时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
