package com.kg.module.oauth2.user.dto;

import com.kg.core.base.dto.BaseDTO;
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
@ApiModel(value = "OauthClientUserDTO", description = "Oauth2 - 用户绑定表")
public class OauthClientUserDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("oauth2的openId")
    private String openId;
}
