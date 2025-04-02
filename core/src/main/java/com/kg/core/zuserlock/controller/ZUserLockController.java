package com.kg.core.zuserlock.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.zuserlock.entity.ZUserLock;
import com.kg.core.zuserlock.service.ZUserLockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户锁定 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/zuserlock/zUserLock")
@Api(tags = "ZUserLockController", value = "用户锁定", description = "用户锁定")
public class ZUserLockController {

    @Resource
    private ZUserLockService zUserLockService;

    @ApiOperation(value = "/zuserlock/zUserLock/list", notes = "永久锁定信息-用户锁定", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('zuserlock:zUserLock:list')")
    public Page<ZUserLock> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                @RequestParam(value = "params", required = false) String params) {
        Page<ZUserLock> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZUserLock> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("lockId")) {
                wrapper.lambda().eq(ZUserLock::getLockId, paramObj.getStr("lockId"));
            }
            if (paramObj.containsKey("userName")) {
                wrapper.lambda().like(ZUserLock::getUserName, paramObj.getStr("userName"));
            }
            if (paramObj.containsKey("lockReason")) {
                wrapper.lambda().eq(ZUserLock::getLockReason, paramObj.getStr("lockReason"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(ZUserLock::getCreateTime, paramObj.getStr("createTime"));
            }
        }
        return zUserLockService.page(pager, wrapper);
    }

    @ApiOperation(value = "/zuserlock/zUserLock/cacheList", notes = "缓存锁定信息-用户锁定", httpMethod = "GET")
    @GetMapping("/cacheList")
    @PreAuthorize("hasAuthority('zuserlock:zUserLock:cacheList')")
    public List<ZUserLock> cacheList() {
        return zUserLockService.getCacheList();
    }

    @ApiOperation(value = "/zuserlock/zUserLock/unlock", notes = "用户解锁", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userNames", value = "待解锁的用户数组", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("/unlock")
    @PreAuthorize("hasAuthority('zuserlock:zUserLock:unlock')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/zuserlock/zUserLock/unlock", logMsg = "用户解锁")
    public void delete(@RequestBody String[] userNames) throws BaseException {
        try {
            zUserLockService.unlock(userNames);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("删除失败！请重试");
        }
    }

}
