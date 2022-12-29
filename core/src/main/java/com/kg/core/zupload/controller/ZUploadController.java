package com.kg.core.zupload.controller;

import com.kg.component.file.FileDTO;
import com.kg.component.file.UploadFileUtils;
import com.kg.component.file.UploadImageUtils;
import com.kg.core.base.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    public List<FileDTO> images(HttpServletRequest request, String path) throws IOException {
        // 图片上传，自动压缩
        return UploadImageUtils.upload(request, StringUtils.hasText(path) ? path : "images");
    }

    @ApiOperation(value = "upload/files", notes = "上传文件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("files")
    public List<FileDTO> files(HttpServletRequest request, String path) throws IOException {
        // 普通文件上传
        return UploadFileUtils.uploadFile(request, StringUtils.hasText(path) ? path : "files");
    }
}
