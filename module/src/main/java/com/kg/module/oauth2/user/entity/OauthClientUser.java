package com.kg.module.oauth2.user.entity;

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
 * Oauth2 - 用户绑定表
 * </p>
 *
 * @author ziro
 * @since 2024-03-04
 */
@Getter
@Setter
@TableName("oauth_client_user")
@ApiModel(value = "OauthClientUser对象", description = "Oauth2 - 用户绑定表")
public class OauthClientUser implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    private String userId;

    @ApiModelProperty("oauth2的openId")
    @TableField(value = "open_id")
    private String openId;
}
