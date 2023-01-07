package com.kg.core.zsafety.controller;

import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 密码安全等设置 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-12-30
 */
@RestController
@RequestMapping("/zsafety/zSafety")
@Api(tags = "ZSafetyController", value = "密码安全等设置", description = "密码安全等设置")
public class ZSafetyController {

    @Resource
    private ZSafetyService zSafetyService;

    @ApiOperation(value = "/zsafety/zSafety/getSafety", notes = "获取密码安全配置", httpMethod = "GET")
    @GetMapping("/getSafety")
    @PreAuthorize("hasAuthority('zsafety:zSafety:getSafety')")
    public ZSafety getSafety() {
        return zSafetyService.getSafety();
    }

    @ApiOperation(value = "/zsafety/zSafety/update", notes = "修改-密码安全等设置", httpMethod = "POST")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('zsafety:zSafety:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ZSafety zSafety) throws BaseException {
        try {
            zSafety.setUpdateTime(LocalDateTime.now());
            zSafetyService.updateById(zSafety);
            zSafetyService.clearCache();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("修改失败！请重试");
        }
    }
}
