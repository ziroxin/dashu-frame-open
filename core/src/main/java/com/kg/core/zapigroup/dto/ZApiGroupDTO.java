package com.kg.core.zapigroup.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.kg.core.base.dto.BaseDTO;
import com.kg.core.zapi.entity.ZApi;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API和分组信息DTO
 *
 * @author ziro
 * @date 2022-08-20 17:20:20
 */
@Getter
@Setter
public class ZApiGroupDTO implements BaseDTO {

    private String apiGroupId;

    private String groupName;

    private Integer groupOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String[] apiIds;
}
