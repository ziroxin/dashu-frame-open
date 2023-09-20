package com.kg.core.zupload.controller;

import cn.hutool.json.JSONUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileChunkDTO;
import com.kg.component.file.dto.FileDTO;
import com.kg.component.file.utils.FileMD5Utils;
import com.kg.component.file.utils.UploadFileChunksUtils;
import com.kg.component.file.utils.UploadFileUtils;
import com.kg.component.file.utils.UploadImageUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.core.annotation.IsResponseResult;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.base.controller.BaseController;
import com.kg.core.exception.BaseException;
import com.kg.core.zupload.dto.WangImageDTO;
import com.kg.core.zupload.dto.WangVideoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件上传控制器
 *
 * @author ziro
 * @date 2022-09-20 11:20:05
 */
@Api(tags = "ZUploadController", value = "文件上传控制器", description = "文件上传控制器")
@RestController
@RequestMapping("upload")
public class ZUploadController implements BaseController {
    @Resource
    private RedisUtils redisUtils;

    @ApiOperation(value = "upload/images", notes = "上传图片", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("images")
    @NoRepeatSubmit
    public List<FileDTO> images(HttpServletRequest request, String path) throws BaseException {
        try {
            // 图片上传，自动压缩
            return UploadImageUtils.upload(request, StringUtils.hasText(path) ? path : "images");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "上传图片失败！请重试");
        }
    }

    @ApiOperation(value = "upload/files", notes = "上传文件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("files")
    @NoRepeatSubmit
    public List<FileDTO> files(HttpServletRequest request, String path) throws BaseException {
        try {
            // 普通文件上传
            return UploadFileUtils.upload(request, StringUtils.hasText(path) ? path : "files");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "上传图片失败！请重试");
        }
    }

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
            String totalChunks = request.getParameter("chunks");
            String dirName = StringUtils.hasText(path) ? path : "chunks";
            // 分片上传
            FileChunkDTO result = UploadFileChunksUtils.upload(request, dirName, chunkNumber, tempFileName, totalChunks);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "分片上传文件失败！请重试");
        }
    }

    @ApiOperation(value = "upload/chunks/resume", notes = "分片上传 - 断点续传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "chunkMD5", value = "分片MD5", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("chunks/resume")
    public FileChunkDTO chunksResume(HttpServletRequest request, String path, String chunkMD5) throws BaseException {
        try {
            if (StringUtils.hasText(chunkMD5)) {
                // 先检测md5
                String md5 = chunkMD5.toUpperCase();
                if (redisUtils.hasKey(md5)) {
                    FileChunkDTO result = JSONUtil.toBean(JSONUtil.parseObj(redisUtils.get(md5)), FileChunkDTO.class);
                    String tempFileName = request.getParameter("name");
                    String chunkNumber = request.getParameter("chunk");
                    if (StringUtils.hasText(tempFileName) && StringUtils.hasText(chunkNumber)) {
                        if (tempFileName.equals(result.getFileOldName()) && result.getChunkNumber().equals(chunkNumber)) {
                            // 文件名和分片号相同，则直接返回
                            System.out.println("文件名和分片号相同");
                            return result;
                        } else {
                            System.out.println("文件名和分片号不同");
                            // 文件名或分片号不同，则拷贝文件返回
                            String tempOldFilePath = FilePathConfig.switchSavePath(result.getFileUrl());
                            UploadFileChunksUtils.copyChunkFile(tempOldFilePath, tempFileName, chunkNumber);
                            result.setChunkNumber(chunkNumber);
                            result.setFileOldName(tempFileName);
                            result.setMerged(false);
                            return result;
                        }
                    }
                } else {
                    return null;
                }
            }
            System.out.println("新上传");
            // 获取分片信息
            String chunkNumber = request.getParameter("chunk");
            String tempFileName = request.getParameter("name");
            String totalChunks = request.getParameter("chunks");
            // 非md5，则上传分片文件
            String dirName = StringUtils.hasText(path) ? path : "chunks";
            FileChunkDTO result = UploadFileChunksUtils.upload(request, dirName, chunkNumber, tempFileName, totalChunks);
            if (!result.isMerged()) {
                // redis保存分片文件的临时文件（保存1天）
                redisUtils.set(result.getFileMd5(), result, 24 * 60 * 60L);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "分片上传文件失败！请重试");
        }
    }

    @ApiOperation(value = "upload/wang/images", notes = "WangEditor的上传图片接口", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("wang/images")
    @NoRepeatSubmit
    @IsResponseResult(value = false)
    public WangImageDTO wangImages(HttpServletRequest request, String path) {
        WangImageDTO result = new WangImageDTO();
        try {
            // 图片上传，自动压缩
            List<FileDTO> upload = UploadImageUtils.upload(request, StringUtils.hasText(path) ? path : "wangEditor/images");
            if (upload != null && upload.size() > 0) {
                return result.success(upload.get(0).getFileUrl(), upload.get(0).getFileOldName(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.error("上传图片失败！");
    }

    @ApiOperation(value = "upload/wang/videos", notes = "WangEditor上传视频接口", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("wang/videos")
    @NoRepeatSubmit
    @IsResponseResult(value = false)
    public WangVideoDTO wangVideos(HttpServletRequest request, String path) {
        WangVideoDTO result = new WangVideoDTO();
        try {
            // 图片上传，自动压缩
            List<FileDTO> upload = UploadFileUtils.upload(request, StringUtils.hasText(path) ? path : "wangEditor/videos");
            if (upload != null && upload.size() > 0) {
                return result.success(upload.get(0).getFileUrl(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.error("上传视频失败！");
    }
}
