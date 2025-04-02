package com.kg.core.zuserlock.entity;

import cn.hutool.json.JSONUtil;
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
 * 用户锁定
 * </p>
 *
 * @author ziro
 * @since 2023-01-05
 */
@Getter
@Setter
@TableName("z_user_lock")
@ApiModel(value = "ZUserLock对象", description = "用户锁定")
public class ZUserLock implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户锁定id")
    @TableId(value = "lock_id", type = IdType.ASSIGN_UUID)
    private String lockId;

    @ApiModelProperty("用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("锁定原因")
    @TableField("lock_reason")
    private String lockReason;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Override
    public String toString() {
        return JSONUtil.parseObj(this, true).toString();
    }
}
