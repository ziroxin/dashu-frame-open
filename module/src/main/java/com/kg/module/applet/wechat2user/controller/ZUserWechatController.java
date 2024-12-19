package com.kg.module.applet.wechat2user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.exception.enums.BaseErrorCode;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zuser.entity.ZUser;
import com.kg.module.applet.wechat2user.dto.ZUserWechatDTO;
import com.kg.module.applet.wechat2user.dto.convert.ZUserWechatConvert;
import com.kg.module.applet.wechat2user.entity.ZUserWechat;
import com.kg.module.applet.wechat2user.service.ZUserWechatService;
import com.kg.module.oauth2.user.entity.OauthClientUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <p>
 * 用户-微信-绑定关系表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/applet/wechat2user/zUserWechat")
@Api(tags = "ZUserWechatController", value = "用户-微信-绑定关系表", description = "用户-微信-绑定关系表")
public class ZUserWechatController {

    @Resource
    private ZUserWechatService zUserWechatService;
    @Resource
    private ZUserWechatConvert zUserWechatConvert;

    /**
     * 用户-微信-解绑
     */
    @GetMapping("/userUnbind")
    @ResponseBody
    public void userUnbind() throws BaseException {
        ZUser currentUser = CurrentUserUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BaseException(BaseErrorCode.LOGIN_ERROR_NOT_LOGIN);
        }
        // 解绑
        zUserWechatService.lambdaUpdate().eq(ZUserWechat::getUserId, currentUser.getUserId()).remove();
    }

    @ApiOperation(value = "/applet/wechat2user/zUserWechat/getById", notes = "详情-用户-微信-绑定关系表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openid", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('applet:wechat2user:zUserWechat:getById')")
    public ZUserWechatDTO getById(String openid) {
        return zUserWechatConvert.entityToDto(zUserWechatService.getById(openid));
    }

    @ApiOperation(value = "/applet/wechat2user/zUserWechat/list", notes = "分页列表-用户-微信-绑定关系表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('applet:wechat2user:zUserWechat:list')")
    public Page<ZUserWechatDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                     @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                     @RequestParam(value = "params", required = false) String params) {
        return zUserWechatService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/applet/wechat2user/zUserWechat/add", notes = "新增-用户-微信-绑定关系表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('applet:wechat2user:zUserWechat:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/applet/wechat2user/zUserWechat/add", logMsg = "新增-用户-微信-绑定关系表")
    public void add(@RequestBody ZUserWechatDTO zUserWechatDTO) throws BaseException {
        try {
            zUserWechatService.add(zUserWechatDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/applet/wechat2user/zUserWechat/update", notes = "修改-用户-微信-绑定关系表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('applet:wechat2user:zUserWechat:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/applet/wechat2user/zUserWechat/update", logMsg = "修改-用户-微信-绑定关系表")
    public void update(@RequestBody ZUserWechatDTO zUserWechatDTO) throws BaseException {
        try {
            zUserWechatService.update(zUserWechatDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/applet/wechat2user/zUserWechat/delete", notes = "删除-用户-微信-绑定关系表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openids", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('applet:wechat2user:zUserWechat:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/applet/wechat2user/zUserWechat/delete", logMsg = "删除-用户-微信-绑定关系表")
    public void delete(@RequestBody String[] openids) throws BaseException {
        try {
            zUserWechatService.delete(Arrays.asList(openids));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }
}
