package com.kg.core.zuser.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 用户角色表
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
@Getter
@Setter
@TableName("z_user_role")
@ApiModel(value = "ZUserRole对象", description = "用户角色表")
public class ZUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("user_id")
    @TableId(value = "user_id")
    private String userId;

    @ApiModelProperty("role_id")
    private String roleId;
}
