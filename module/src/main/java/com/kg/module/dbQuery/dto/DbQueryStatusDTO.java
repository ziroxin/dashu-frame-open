package com.kg.module.dbQuery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * db-query 模块状态 DTO（前端据此决定功能是否可用/是否显示写模式）
 *
 * @author ziro
 * @date 2026/3/9
 */
@Data
@ApiModel("数据库查询工具状态")
public class DbQueryStatusDTO {

    @ApiModelProperty("模块是否启用（false 时接口未注册，前端提示功能已关闭）")
    private Boolean enabled;

    @ApiModelProperty("是否允许写操作（开发管理员二次确认可用）")
    private Boolean writeEnabled;

    @ApiModelProperty("查询是否只读模式")
    private Boolean readOnly;

    @ApiModelProperty("当前用户是否开发管理员")
    private Boolean developer;
}
