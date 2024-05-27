package com.kg.core.retgister.controller;

import com.kg.component.utils.MyRSAUtils;
import com.kg.component.utils.PasswordRegexUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.retgister.dto.RegisterFormDTO;
import com.kg.core.retgister.service.RegisterService;
import com.kg.core.zcaptcha.service.ZCaptchaService;
import com.kg.core.zorg.dto.ZOrganizationTreeSelectDTO;
import com.kg.core.zorg.service.ZOrganizationService;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
import com.kg.module.config.entity.ZConfig;
import com.kg.module.config.service.ZConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 注册 - API
 *
 * @author ziro
 * @date 2024/5/27 8:57
 */
@RestController
@RequestMapping("/register")
@Api(tags = "RegisterController", value = "注册", description = "注册")
public class RegisterController {
    @Resource
    private ZOrganizationService orgService;
    @Resource
    private RegisterService registerService;
    @Resource
    private ZCaptchaService captchaService;
    @Resource
    private ZSafetyService safetyService;
    @Resource
    private ZConfigService configService;

    @ApiOperation(value = "/register/new", notes = "注册新用户", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/new")
    public void registerNewUser(@RequestBody RegisterFormDTO regFormDTO) throws BaseException {
        // 0. 注册配置（是否开启注册功能）
        if (getRegConfig() == false) {
            throw new BaseException("注册功能已关闭！");
        }
        // 1. 验证码
        if (!StringUtils.hasText(regFormDTO.getYzm())) {
            throw new BaseException("请输入验证码！");
        }
        if (!captchaService.checkCaptcha(regFormDTO.getCodeUuid(), regFormDTO.getYzm())) {
            throw new BaseException("验证码错误！请检查");
        }
        // 2. 参数解密（前端公钥加密，后端私钥解密）
        if (regFormDTO.getIsEncrypt() != null && regFormDTO.getIsEncrypt()) {
            regFormDTO.setUserName(MyRSAUtils.decryptPrivate(regFormDTO.getUserName()));
            regFormDTO.setPassword(MyRSAUtils.decryptPrivate(regFormDTO.getPassword()));
        }
        // 3. 验证密码安全性(判断密码规则)
        ZSafety safety = safetyService.getSafety();
        if (!PasswordRegexUtils.judgeLength(safety.getStartLength(), safety.getEndLength(), regFormDTO.getPassword())) {
            // 长度
            throw new BaseException(safety.getPrompt());
        }
        if (!PasswordRegexUtils.judgeRegex(safety.getLowercase().intValue(), safety.getUppercase().intValue(),
                safety.getNumbers().intValue(), safety.getSpecialCharacters().intValue(), regFormDTO.getPassword())) {
            // 强度
            throw new BaseException(safety.getPrompt());
        }
        if (1 == safety.getBanUsername().intValue()) {
            // 是否包含用户名
            if (regFormDTO.getPassword().contains(regFormDTO.getUserName())) {
                throw new BaseException("密码不能包含用户名！");
            }
        }
        // 4. 注册
        registerService.add(regFormDTO);
    }

    @ApiOperation(value = "/register/org/list", notes = "获取组织列表", httpMethod = "GET")
    @GetMapping("/org/list")
    public List<ZOrganizationTreeSelectDTO> listOrg() {
        return orgService.treeForSelect(null);
    }

    @ApiOperation(value = "/register/config/reg", notes = "获取注册开关", httpMethod = "GET")
    @GetMapping("/config/reg")
    public boolean getRegConfig() {
        Optional<ZConfig> one = configService.lambdaQuery().eq(ZConfig::getCfgKey, "sys.register.isOpen").oneOpt();
        if (one.isPresent()) {
            return "true".equals(one.get().getCfgValue());
        }
        return false;
    }

}
