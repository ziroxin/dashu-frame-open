package com.kg.core.zpermission.entity;

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
 * 资源权限表
 * </p>
 *
 * @author ziro
 * @since 2022-05-09
 */
@Getter
@Setter
@TableName("z_permission")
@ApiModel(value = "ZPermission对象", description = "资源权限表")
public class ZPermission implements BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("资源ID")
    @TableId(value = "permission_id", type = IdType.ASSIGN_UUID)
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
    @TableField("no_redirect")
    private String noRedirect;

    @ApiModelProperty("默认false,为true时不被<keep-alive>缓存")
    @TableField("no_cache")
    private int noCache;

    @ApiModelProperty("默认true,为false时不在面包屑中显示")
    @TableField("breadcrumb")
    private int breadcrumb;

    @ApiModelProperty("默认false,为true时固定在标签里")
    @TableField("affix")
    private int affix;

    @ApiModelProperty("本路由hidden时，菜单栏高亮显示的路由")
    @TableField("active_menu")
    private String activeMenu;

    @ApiModelProperty("资源顺序")
    private Integer permissionOrder;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("最后更新时间")
    private LocalDateTime updateTime;
}
