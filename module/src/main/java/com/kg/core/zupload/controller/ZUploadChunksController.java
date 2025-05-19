package com.kg.core.zupload.controller;

import cn.hutool.json.JSONUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileChunkDTO;
import com.kg.component.file.utils.UploadFileChunksUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.base.controller.BaseController;
import com.kg.core.exception.BaseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件分片上传 - 控制器
 *
 * @author ziro
 * @date 2022-09-20 11:20:05
 */
@Api(tags = "ZUploadChunksController", value = "文件分片上传控制器", description = "文件分片上传控制器")
@RestController
@RequestMapping("upload")
public class ZUploadChunksController implements BaseController {
    @Resource
    private RedisUtils redisUtils;

    @ApiOperation(value = "upload/chunks", notes = "分片上传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("chunks")
    public FileChunkDTO chunks(HttpServletRequest request, String path) throws BaseException {
        try {
            // 获取分片信息
            String chunkNumber = request.getParameter("chunk");
            String tempFileName = request.getParameter("name");
            String uploadId = request.getParameter("uploadId");// 本次上传的唯一id
            String totalChunks = request.getParameter("chunks");
            String dirName = StringUtils.hasText(path) ? path : "chunks";
            // 分片上传
            FileChunkDTO result = UploadFileChunksUtils.upload(request, dirName, chunkNumber, tempFileName, uploadId, totalChunks);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "分片上传文件失败！请重试");
        }
    }

    @ApiOperation(value = "upload/chunks/resume/first", notes = "断点续传1：根据分片MD5检测文件是否已上传", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chunkNumber", value = "分片索引", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "tempFileName", value = "原始文件名", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "uploadId", value = "本次上传的唯一id", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "totalChunks", value = "总分片数", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "chunkMD5", value = "分片MD5", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("chunks/resume/first")
    public FileChunkDTO chunksResumeFirst(String chunkNumber, String tempFileName, String uploadId,
                                          String totalChunks, String chunkMD5, String path) throws BaseException {
        try {
            if (!StringUtils.hasText(chunkMD5)) {
                throw new BaseException("分片MD5不能为空！");
            }
            // 有md5参数，则检测是否已上传
            String md5 = chunkMD5.toUpperCase();
            if (redisUtils.hasKey(md5)) {
                FileChunkDTO result = JSONUtil.toBean(JSONUtil.parseObj(redisUtils.get(md5)), FileChunkDTO.class);
                // 拷贝redis中已缓存的分片（md5存在，说明有以前断开的上传片段，所以需要拷贝过来，供本次上传使用）
                String tempOldFilePath = FilePathConfig.switchSavePath(result.getFileUrl());
                UploadFileChunksUtils.copyChunkFile(tempOldFilePath, tempFileName, uploadId, chunkNumber);
                // 判断是否需要合并
                if (UploadFileChunksUtils.isAllChunksUploaded(tempFileName, uploadId, totalChunks)) {
                    // 全部上传完成，合并，并清理redis
                    if (redisUtils.hasKey(uploadId)) {
                        for (String md5Key : redisUtils.get(uploadId).toString().split(",")) {
                            redisUtils.delete(md5Key);
                        }
                        redisUtils.delete(uploadId);
                    }
                    String dirName = StringUtils.hasText(path) ? path : "chunksResume";
                    return UploadFileChunksUtils.mergeChunks(dirName, tempFileName, uploadId, totalChunks);
                } else {
                    // 未上传完成所有上传，返回分片上传结果
                    result.setChunkNumber(chunkNumber);
                    result.setFileOldName(tempFileName);
                    result.setMerged(false);
                    return result;
                }
            } else {
                // redis中无该md5，返回null，前端需上传新文件
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "分片上传文件失败！请重试");
        }
    }

    @ApiOperation(value = "upload/chunks/resume/second", notes = "断点续传2：上传新的分片文件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "chunkNumber", value = "分片索引", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "tempFileName", value = "原始文件名", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "uploadId", value = "本次上传的唯一id", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "totalChunks", value = "总分片数", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String"),
    })
    @PostMapping("chunks/resume/second")
    public FileChunkDTO chunksResumeSecond(HttpServletRequest request, String chunkNumber, String tempFileName,
                                           String uploadId, String totalChunks, String path) throws BaseException {
        try {
            String dirName = StringUtils.hasText(path) ? path : "chunks";
            FileChunkDTO result = UploadFileChunksUtils.upload(request, dirName, chunkNumber, tempFileName, uploadId, totalChunks);
            if (result.isMerged()) {
                // 已合并完成，清理redis缓存
                if (redisUtils.hasKey(uploadId)) {
                    for (String md5Key : redisUtils.get(uploadId).toString().split(",")) {
                        redisUtils.delete(md5Key);
                    }
                    redisUtils.delete(uploadId);
                }
            } else {
                // 未合并，缓存分片文件信息（保存1天）
                redisUtils.set(result.getFileMd5(), result, 24 * 60 * 60L);
                String md5s = redisUtils.hasKey(uploadId) ? redisUtils.get(uploadId).toString() + "," : "";
                redisUtils.set(uploadId, md5s + result.getFileMd5(), 24 * 60 * 60L);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "分片上传文件失败！请重试");
        }
    }
}
