package com.kg.core.zuser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author ziro
 * @since 2022-05-01
 */
@Getter
@Setter
@TableName("z_user")
@ApiModel(value = "ZUser对象", description = "用户表")
public class ZUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    private String userId;

    @ApiModelProperty("用户所在部门ID")
    private String orgId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("性别(0未知1男2女)")
    private String sex;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("简介")
    private String introduce;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("用户状态(0禁用1正常)")
    private String status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("最后更新时间")
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "ZUser{" +
                "userId=" + userId +
                ", userName=" + userName +
                ", password=" + password +
                ", sex=" + sex +
                ", nickName=" + nickName +
                ", introduce=" + introduce +
                ", avatar=" + avatar +
                ", name=" + name +
                ", phone=" + phone +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                "}";
    }
}
