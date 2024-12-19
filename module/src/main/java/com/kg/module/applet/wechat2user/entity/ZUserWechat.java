package com.kg.module.applet.wechat2user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kg.core.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户-微信-绑定关系表
 * </p>
 *
 * @author ziro
 * @since 2024-12-17
 */
@Getter
@Setter
@TableName("z_user_wechat")
@ApiModel(value = "ZUserWechat对象", description = "用户-微信-绑定关系表")
public class ZUserWechat implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("微信openid")
    @TableId(value = "openid", type = IdType.ASSIGN_UUID)
    private String openid;

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private String userId;
}
