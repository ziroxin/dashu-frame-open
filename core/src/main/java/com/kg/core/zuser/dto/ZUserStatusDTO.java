package com.kg.core.zuser.dto;

import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户状态实体
 *
 * @author ziro
 * @date 2023-01-16 09:10:07
 */
@Getter
@Setter
public class ZUserStatusDTO implements BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("userId数组")
    private String[] userIds;

    @ApiModelProperty("用户状态(0禁用1正常)")
    private String status;
}
