package com.kg.core.zupload.controller;

import cn.hutool.core.io.FileUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import com.kg.component.file.utils.RemoveFileUtils;
import com.kg.component.file.utils.UploadFileUtils;
import com.kg.component.file.utils.UploadImageUtils;
import com.kg.component.pdf.ExcelToPdfUtils;
import com.kg.component.pdf.WordToPdfUtils;
import com.kg.core.annotation.NoRepeatSubmit;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 普通文件上传 - 控制器
 *
 * @author ziro
 * @date 2022-09-20 11:20:05
 */
@Api(tags = "ZUploadController", value = "普通文件上传控制器", description = "普通文件上传控制器")
@RestController
@RequestMapping("/upload")
public class ZUploadController implements BaseController {

    @ApiOperation(value = "/upload/images", notes = "上传图片", httpMethod = "POST")
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

    @ApiOperation(value = "/upload/files", notes = "上传文件", httpMethod = "POST")
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

    @ApiOperation(value = "/upload/deleteFile", notes = "删除文件接口", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileUrl", value = "文件地址", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/deleteFile")
    public void deleteFile(String fileUrl) {
        RemoveFileUtils.remove(fileUrl);
    }

    @ApiOperation(value = "/upload/preview", notes = "预览文件接口", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileUrl", value = "文件地址", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/preview")
    public String preview(String fileUrl, String fileExtend) throws BaseException {
        try {
            String pdfUrl = fileUrl.substring(fileUrl.lastIndexOf(".")) + ".pdf";
            if (FileUtil.exist(FilePathConfig.switchSavePath(pdfUrl))) {
                return pdfUrl;// 文件已存在，直接返回
            }
            if ("xlsx".equals(fileExtend)) {
                FileDTO pdf = ExcelToPdfUtils.toPdf(fileUrl);
                return pdf.getFileUrl();
            } else if ("docx".equals(fileExtend)) {
                FileDTO pdf = WordToPdfUtils.toPdf(fileUrl);
                return pdf.getFileUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BaseException("生成预览文件失败！请重试");
    }
}
