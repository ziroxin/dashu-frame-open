package com.kg.core.xss.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.log.StaticLog;
import com.kg.DashuApplication;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Xss 忽略名单管理
 *
 * @author ziro
 * @date 2023/9/8 17:55
 */
@RestController
@RequestMapping("/xss/ignore")
@Api(tags = "XssIgnoreController", value = "Xss忽略名单管理", description = "Xss忽略名单管理")
public class XssIgnoreController {

    @Resource
    private ConfigurableApplicationContext context;

    @ApiOperation(value = "/xss/ignore/read", notes = "读取Xss忽略名单", httpMethod = "GET")
    @GetMapping("/read")
    public List<String> read() {
        return FileUtil.readLines("xss.ignore", CharsetUtil.defaultCharset());
    }

    @ApiOperation(value = "/xss/ignore/write", notes = "写入（保存）Xss忽略名单", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lines", value = "Xss忽略名单", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("/write")
    public void write(@RequestBody String[] lines) {
        FileUtil.writeLines(Arrays.asList(lines), "xss.ignore", CharsetUtil.defaultCharset());
    }


    @ApiOperation(value = "/xss/ignore/restart", notes = "重启应用", httpMethod = "GET")
    @GetMapping("/restart")
    public void restartApplication() throws BaseException {
        try {
            if ((LoginConstant.DEVELOPER_USER_IDS + ",").contains(CurrentUserUtils.getCurrentUser().getUserId() + ",")) {
                StaticLog.warn("用户重启了应用！");
                Thread thread = new Thread(() -> {
                    // 关闭当前应用上下文
                    context.close();
                    // 重新启动应用程序
                    SpringApplication.run(DashuApplication.class);
                });
                thread.setDaemon(false);
                thread.start();
            } else {
                throw new BaseException("您不是开发管理员，不能进行该操作！");
            }
        } catch (Exception e) {
            throw new BaseException("应用重启失败！Error:" + e.getMessage());
        }
    }
}
