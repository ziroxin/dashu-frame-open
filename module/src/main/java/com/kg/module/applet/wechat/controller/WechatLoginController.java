package com.kg.module.applet.wechat.controller;

import cn.hutool.json.JSONUtil;
import com.kg.component.utils.MyRSAUtils;
import com.kg.component.wechat.applet.WechatAppletLoginUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zcaptcha.service.ZCaptchaService;
import com.kg.core.zlogin.dto.LoginFormDTO;
import com.kg.core.zlogin.dto.LoginSuccessDTO;
import com.kg.core.zlogin.service.ZLoginService;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import com.kg.module.applet.wechat.dto.WechatLoginFormDTO;
import com.kg.module.applet.wechat2user.entity.ZUserWechat;
import com.kg.module.applet.wechat2user.service.ZUserWechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 小程序登录接口
 *
 * @author ziro
 * @date 2024/12/13 9:20
 */
@RestController
@RequestMapping("/applet/wechat/login")
@Api(tags = "小程序登录", value = "小程序登录接口", description = "小程序登录接口")
public class WechatLoginController {

    @Resource
    private ZLoginService loginService;
    @Resource
    private IZUserService userService;
    @Resource
    private ZUserWechatService userWechatService;
    @Resource
    private WechatAppletLoginUtils wechatAppletLoginUtils;
    @Resource
    private ZCaptchaService captchaService;
    @Value("${com.kg.login.is-yzm}")
    private boolean IS_YZM;

    @ApiOperation(value = "/applet/wechat/login/loginByCode", notes = "使用微信凭证code登录，无需用户名和密码", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/loginByCode")
    public LoginSuccessDTO loginByCode(@RequestBody WechatLoginFormDTO loginForm) throws BaseException {
        ZUserWechat bind = getBindByCode(loginForm.getCode());
        if (bind != null && StringUtils.hasText(bind.getUserId())) {
            // 尝试登录
            ZUser user = userService.getById(bind.getUserId());
            if (user != null) {
                LoginFormDTO login = new LoginFormDTO();
                login.setUserName(user.getUserName());
                login.setPassword(user.getPassword());
                return loginService.login(login);
            }
        }
        return null;
    }

    @ApiOperation(value = "/applet/wechat/login/login", notes = "微信登录接口，需要用户名和密码", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/login")
    public LoginSuccessDTO login(@RequestBody WechatLoginFormDTO loginForm) throws BaseException {
        // 1. 先查询是否绑定过
        ZUserWechat bind = getBindByCode(loginForm.getCode());
        if (bind != null && StringUtils.hasText(bind.getUserId())) {
            // 2. 已绑定，尝试登录
            ZUser user = userService.getById(bind.getUserId());
            if (user == null) {
                return null;
            }
            LoginFormDTO login = new LoginFormDTO();
            login.setUserName(user.getUserName());
            login.setPassword(user.getPassword());
            return loginService.login(login);

        } else {
            // 3. 若未绑定，则进行绑定
            if (IS_YZM) {
                // 验证码
                if (!StringUtils.hasText(loginForm.getYzm())) {
                    throw new BaseException("请输入验证码！");
                }
                if (!captchaService.checkCaptcha(loginForm.getCodeUuid(), loginForm.getYzm())) {
                    throw new BaseException("验证码错误！请检查");
                }
            }
            // 若参数解密（前端公钥加密，后端私钥解密）
            if (loginForm.getIsEncrypt() != null && loginForm.getIsEncrypt()) {
                loginForm.setUserName(MyRSAUtils.decryptPrivate(loginForm.getUserName()));
                loginForm.setPassword(MyRSAUtils.decryptPrivate(loginForm.getPassword()));
            }
            // 开始登录
            LoginFormDTO login = JSONUtil.toBean(JSONUtil.parseObj(loginForm), LoginFormDTO.class);
            LoginSuccessDTO result = loginService.login(login);
            // 登录成功，绑定openid和当前用户
            ZUser user = CurrentUserUtils.getCurrentUserByToken(result.getAccessToken());
            ZUserWechat entity = new ZUserWechat();
            entity.setOpenid(bind.getOpenid());
            entity.setUserId(user.getUserId());
            userWechatService.save(entity);
            return result;
        }
    }

    private ZUserWechat getBindByCode(String code) throws BaseException {
        if (!StringUtils.hasText(code)) {
            throw new BaseException("微信登录异常！获取登录凭证（code）失败");
        }
        // 微信code获取openid
        String openid = wechatAppletLoginUtils.getOpenidByJsCode(code);
        if (!StringUtils.hasText(openid)) {
            throw new BaseException("微信登录异常！获取openid失败");
        }
        List<ZUserWechat> list = userWechatService.lambdaQuery().eq(ZUserWechat::getOpenid, openid).list();
        if (list.size() > 1) {
            throw new BaseException("微信登录异常！已绑定多个用户，请先解绑！");
        } else if (list.size() == 1) {
            return list.get(0);// 查到已绑定用户，返回结果
        } else {
            // 未绑定用户，返回空对象(带openid)
            ZUserWechat result = new ZUserWechat();
            result.setOpenid(openid);
            return result;
        }
    }
}
