package com.kg.core.zupload.controller;

import com.kg.component.file.FileDTO;
import com.kg.component.file.UploadFileUtils;
import com.kg.component.file.UploadImageUtils;
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
