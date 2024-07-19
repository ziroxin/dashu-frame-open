package com.kg.component.oss.aliyun.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.log.StaticLog;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.kg.component.oss.aliyun.config.OssBaseConfig;
import com.kg.component.oss.aliyun.dto.OssClientUploadDTO;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.UrlParams2JsonUtils;
import com.kg.core.exception.BaseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Date;

/**
 * Oss 客户端直传 - 控制器
 *
 * @author ziro
 * @date 2024/7/16 15:59
 */
@RestController
@RequestMapping("/oss/client/upload")
@Api(tags = "OssClientUploadController", value = "Oss客户端直传", description = "Oss客户端直传")
public class OssClientUploadController {
    @Resource
    private OssBaseConfig config;
    @Resource
    private RedisUtils redisUtils;

    @ApiOperation(value = "/oss/client/upload/token", notes = "获取客户端直传 - 上传凭证", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "上传文件夹", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "oldFileName", value = "原文件名", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "maxSize", value = "上传大小限制（单位b）", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/token")
    public OssClientUploadDTO getUploadToken(String path, String oldFileName, Integer maxSize) {
        OSSClient client = new OSSClient(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        try {
            PolicyConditions policyConds = new PolicyConditions();
            // 用户上传文件时指定的前缀
            path = (StringUtils.hasText(path) ? path + "/" : "files/").replace("//", "/");
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, path);
            if (maxSize != null) {
                // 上传大小限制
                policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
            }
            // 上传凭证的有效期
            long expireEndTime = System.currentTimeMillis() + config.getUploadTokenExpire() * 1000;
            Date expiration = new Date(expireEndTime);
            // 其他配置
            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            OssClientUploadDTO dto = new OssClientUploadDTO();
            dto.setOSSAccessKeyId(config.getAccessKeyId());
            dto.setPolicy(BinaryUtil.toBase64String(postPolicy.getBytes("utf-8")));
            dto.setSignature(client.calculatePostSignature(postPolicy));
            String saveFileName = GuidUtils.getUuid32();
            dto.setKey(path + saveFileName + oldFileName.substring(oldFileName.lastIndexOf(".")));
            dto.setHost(config.getHost());
            dto.setExpire(String.valueOf(expireEndTime / 1000));
            // 回调配置 @See:https://help.aliyun.com/zh/oss/use-cases/overview-20?spm=a2c4g.11186623.0.0.2f4f5febxZBpOm
            JSONObject callback = new JSONObject();
            callback.put("callbackUrl", config.getCallbackUrl());// 回调地址
            // 回调参数 @See:https://help.aliyun.com/zh/oss/developer-reference/callback?spm=a2c4g.11186623.0.0.75e74cb2K0YA7D
            callback.put("callbackBody", "fileName=${object}&fileSize=${size}&fileOldName=${x:ofn}" +
                    "&md5=${contentMd5}&fileId=${x:fid}");
            callback.put("callbackBodyType", "application/json");// 回调参数类型
            dto.setCallback(BinaryUtil.toBase64String(callback.toString().getBytes()));
            // 回调自定义参数（例如：x:fileUuid，回调时会在 fileUuid 中原样返回）
            JSONObject callbackVar = new JSONObject();
            callbackVar.put("x:fid", saveFileName);
            callbackVar.put("x:ofn", oldFileName);
            dto.setCallbackVar(callbackVar);
            // 返回上传凭证
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
        return null;
    }

    @ApiOperation(value = "/oss/client/upload/deleteFromCache", notes = "OSS删除缓存文件接口", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "文件ID", paramType = "query", required = true, dataType = "String")
    })
    @DeleteMapping("/deleteFromCache")
    public boolean deleteFromCache(String fileId) {
        if (redisUtils.hasKey(fileId)) {
            JSONObject file = (JSONObject) redisUtils.get(fileId);
            try {
                delete(file.getStr("fileName"));
                return true;
            } catch (BaseException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    @ApiOperation(value = "/oss/client/upload/delete", notes = "OSS删除文件接口", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "Oss存储名（文件夹名+文件名）", paramType = "query", required = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    public void delete(String fileName) throws BaseException {
        // 删除文件
        OSSClient ossClient = new OSSClient(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(config.getBucket(), fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("删除文件失败！请重试");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * OSS上传成功后 - 回调接口
     * 只接受阿里云的回调请求
     */
    @PostMapping("/callback")
    public JSONObject callback(HttpServletRequest request, @RequestBody String ossCallbackBody) {
        JSONObject result = new JSONObject();
        // 校验回调地址是否合法
        String pubKeyAddr = new String(BinaryUtil.fromBase64String(request.getHeader("x-oss-pub-key-url")));
        if (!pubKeyAddr.startsWith("http://gosspublic.alicdn.com/")
                && !pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")) {
            StaticLog.warn("不是阿里云oss地址回调，疑似伪造请求");
            result.put("msg", "不是阿里云oss地址回调，疑似伪造请求");
            result.put("success", false);
            return result;
        }
        JSONObject body = UrlParams2JsonUtils.toJson(ossCallbackBody);
        String name = body.getStr("fileName");
        body.set("fileUrl", config.getDomain() + "/" + name);
        body.set("fileExtend", name.substring(name.lastIndexOf(".") + 1));
        System.out.println(body);
        String fileId = body.getStr("fileId");
        // 文件存入缓存（20分钟后过期）
        redisUtils.set(fileId, body, 60 * 20l);
        result.put("msg", "上传成功");
        result.put("success", true);
        result.put("id", fileId);
        return result;
    }
}
