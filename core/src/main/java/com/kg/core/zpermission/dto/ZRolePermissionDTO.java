package com.kg.core.zpermission.dto;

import com.kg.core.base.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限资源DTO
 *
 * @author ziro
 * @date 2022/5/10 21:56
 */
@Getter
@Setter
public class ZRolePermissionDTO implements BaseDTO {

    @ApiModelProperty("资源ID")
    private String permissionId;

    @ApiModelProperty("父级ID")
    private String parentId;

    @ApiModelProperty("资源名称")
    private String permissionName;

    @ApiModelProperty("资源描述")
    private String permissionDescription;

    @ApiModelProperty("资源类型（0路由1页面资源2外链3其他）")
    private String permissionType;

    @ApiModelProperty("资源标题")
    private String permissionTitle;

    @ApiModelProperty("资源图标")
    private String permissionIcon;

    @ApiModelProperty("资源标记（路由/外链/标记）")
    private String permissionRouter;

    @ApiModelProperty("组件地址")
    private String permissionComponent;

    @ApiModelProperty("自定义配置（JSON）")
    private String permissionConfig;

    @ApiModelProperty("是否可见（0隐藏1显示）")
    private int permissionIsShow;

    @ApiModelProperty("是否禁用（0禁用1启用）")
    private int permissionIsEnabled;

    @ApiModelProperty("=noRedirect时,在面包屑导航不可点击")
    private String noRedirect;

    @ApiModelProperty("默认false,为true时不被<keep-alive>缓存")
    private int noCache;

    @ApiModelProperty("默认true,为false时不在面包屑中显示")
    private int breadcrumb;

    @ApiModelProperty("默认false,为true时固定在标签里")
    private int affix;

    @ApiModelProperty("本路由hidden时，菜单栏高亮显示的路由")
    private String activeMenu;

    @ApiModelProperty("资源顺序")
    private Integer permissionOrder;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("最后更新时间")
    private LocalDateTime updateTime;

    // 子资源
    private List<ZRolePermissionDTO> children;
    // 按钮等其他资源
    private List<ZRolePermissionDTO> buttonList;
    // 角色是否有此权限
    private boolean hasPermission;

}
