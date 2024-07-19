package com.kg.component.oss.aliyun.controller;

import cn.hutool.json.JSONUtil;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.kg.component.oss.aliyun.config.OssBaseConfig;
import com.kg.component.utils.TimeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URL;
import java.util.HashMap;

/**
 * Oss 文件访问临时凭证 - 控制器
 *
 * @author ziro
 * @date 2024/7/18 16:59
 */
@RestController
@RequestMapping("/oss/file/read")
@Api(tags = "OssFileReadController", value = "Oss文件访问临时凭证", description = "Oss文件访问临时凭证")
public class OssFileReadController {
    @Resource
    private OssBaseConfig config;

    @ApiOperation(value = "/oss/file/read/sts/url", notes = "获取sts临时访问url", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "Oss存储名（文件夹名+文件名）", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "params", value = "用户参数", required = false, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "headers", value = "用户请求头", required = false, paramType = "query", dataType = "string")
    })
    @GetMapping("/sts/url")
    public String getStsUrl(String fileName, String params, String headers) {
        OSSClient ossClient = new OSSClient(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(config.getBucket(), fileName, HttpMethod.GET);
            // 设置sts签名过期时间
            request.setExpiration(TimeUtils.now().addSecond(config.getStsExpire()).toDate());

            // 用户自定义参数
            if (StringUtils.hasText(params)) {
                HashMap<String, String> userMetadata = JSONUtil.toBean(params, HashMap.class);
                request.setUserMetadata(userMetadata);
            }
            // 用户自定义请求头
            if (StringUtils.hasText(headers)) {
                HashMap<String, String> headersMap = JSONUtil.toBean(headers, HashMap.class);
                request.setHeaders(headersMap);
            }

            // 生成sts临时访问url
            URL url = ossClient.generatePresignedUrl(request);
            System.out.println(url.toString());
            return url.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }
}
