package com.kg.core.security.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Security 忽略名单管理
 *
 * @author ziro
 * @date 2023/9/8 17:55
 */
@RestController
@RequestMapping("/security/ignore")
@Api(tags = "SecurityIgnoreController", value = "Security忽略名单管理", description = "Security忽略名单管理")
public class SecurityIgnoreController {

    @ApiOperation(value = "/security/ignore/read", notes = "读取Security忽略名单", httpMethod = "GET")
    @GetMapping("/read")
    public List<String> read() {
        return FileUtil.readLines("security.ignore", CharsetUtil.defaultCharset());
    }

    @ApiOperation(value = "/security/ignore/write", notes = "写入（保存）Security忽略名单", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lines", value = "Security忽略名单", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("/write")
    public void write(@RequestBody String[] lines) {
        FileUtil.writeLines(Arrays.asList(lines), "security.ignore", CharsetUtil.defaultCharset());
    }

    // TODO: 动态刷新Spring Security配置（目前没有实现思路）
}
