package com.kg.module.test.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ziro
 * @date 2023-01-04 15:27:21
 */
@RestController
@RequestMapping("test")
public class TestController {

    /**
     * 用途：公开接口
     * 例如：登录、登出、验证码等
     * 在security.ignore放开的接口，所有人都可以访问
     */
    @GetMapping("abc")
    public String abc() {
        return "get abc test! no user";
    }

    /**
     * 用途：公共权限的接口
     * 例如：修改密码（登录用户都应该有）
     * 无权限标签（PreAuthorize)的接口，只要用户登录，就可以访问
     */
    @GetMapping("abc1")
    public String abc1() {
        return "get abc1 test! no login";
    }

    /**
     * 用途：正常业务接口
     * 有权限标签，必须对应角色用户才能访问
     */
    @GetMapping("abc2")
    @PreAuthorize("hasAuthority('test:abc2')")
    public String abc2() {
        return "get abc2 test! no login";
    }

    /**
     * 不推荐的方式（既然要控制权限，就不要放入忽略列表）
     * 有权限标签，必须对应角色用户才能访问，在security.ignore放开也无效
     */
    @GetMapping("abc3")
    @PreAuthorize("hasAuthority('test:abc3')")
    public String abc3() {
        return "get abc3 test! no login";
    }
}
