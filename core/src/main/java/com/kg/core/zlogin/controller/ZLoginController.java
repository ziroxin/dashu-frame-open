package com.kg.core.zlogin.controller;

import com.kg.core.base.controller.BaseController;
import com.kg.core.exception.BaseException;
import com.kg.core.zlogin.dto.LoginFormDTO;
import com.kg.core.zlogin.dto.LoginSuccessDTO;
import com.kg.core.zlogin.service.ZLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户登录
 *
 * @author ziro
 * @date 2022/5/2 9:46
 */
@Api(tags = "ZLoginController", value = "用户登录", description = "用户登录控制器")
@RestController
@Validated
@RequestMapping("login")
public class ZLoginController implements BaseController {

    @Resource
    private ZLoginService zLoginService;

    @ApiOperation(value = "登录", notes = "登录接口", httpMethod = "POST")
    @PostMapping("login")
    public LoginSuccessDTO login(@RequestBody LoginFormDTO loginForm) throws BaseException {

        return zLoginService.login(loginForm);
    }

    @ApiOperation(value = "登出", notes = "退出接口", httpMethod = "POST")
    @GetMapping("logout")
    public void logout() {
        zLoginService.logout();
    }

}
