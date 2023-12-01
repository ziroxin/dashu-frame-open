package com.kg.core.xss.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.kg.core.exception.BaseException;
import com.kg.core.xss.XssConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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


    @ApiOperation(value = "/xss/ignore/reload", notes = "重新加载Xss忽略名单", httpMethod = "GET")
    @GetMapping("/reload")
    public void reload() throws BaseException {
        try {
            // 读取忽略列表
            List<String> antMatchers = FileUtil.readLines("xss.ignore", CharsetUtil.defaultCharset());
            XssConstant.XSS_INGORE_URL_LIST = antMatchers.stream()
                    .filter(url -> StringUtils.hasText(url) && !url.startsWith("#"))
                    .collect(Collectors.toList()).toArray(new String[]{});
        } catch (Exception e) {
            throw new BaseException("重新加载失败！Error:" + e.getMessage());
        }
    }
}
