package com.kg.core.zuserpassword.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户密码修改记录
 * </p>
 *
 * @author ziro
 * @since 2023-01-04
 */
@Getter
@Setter
@TableName("z_user_password")
@ApiModel(value = "ZUserPassword对象", description = "用户密码修改记录")
public class ZUserPassword implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;

    @ApiModelProperty("旧密码")
    @TableField("old_password")
    private String oldPassword;

    @ApiModelProperty("修改密码时间")
    @TableField("edit_password_time")
    private LocalDateTime editPasswordTime;
}
