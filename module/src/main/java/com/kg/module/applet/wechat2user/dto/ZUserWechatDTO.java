package com.kg.module.applet.wechat2user.dto;

import com.kg.core.base.dto.BaseDTO;
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
@ApiModel(value = "ZUserWechatDTO", description = "用户-微信-绑定关系表")
public class ZUserWechatDTO implements BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("微信openid")
    private String openid;

    @ApiModelProperty("用户ID")
    private String userId;
}
