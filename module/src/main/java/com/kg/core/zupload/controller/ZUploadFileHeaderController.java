package com.kg.core.zupload.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.kg.component.file.utils.FileTypeUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件头hash管理 - 控制器
 *
 * @author ziro
 * @date 2024/2/19 9:04
 */
@RestController
public class ZUploadFileHeaderController {

    @ApiOperation(value = "upload/fileType/read", notes = "获取文件类型列表", httpMethod = "GET")
    @GetMapping("upload/fileType/read")
    public List<String> getFileTypes() {
        return FileUtil.readLines("fileTypeMap.properties", CharsetUtil.defaultCharset());
    }

    @ApiOperation(value = "upload/fileType/write", notes = "写入（保存）文件类型列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lines", value = "文件类型列表", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("upload/fileType/write")
    public void write(@RequestBody String[] lines) {
        FileUtil.writeLines(Arrays.asList(lines), "fileTypeMap.properties", CharsetUtil.defaultCharset());
        FileTypeUtils.loadFileType();
        System.out.println(FileTypeUtils.FILE_TYPE_MAP.size());
    }

    @ApiOperation(value = "upload/fileHeader", notes = "获取文件头", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("upload/fileHeader")
    @NoRepeatSubmit
    public String fileHeader(HttpServletRequest request) throws BaseException {
        try {
            List<String> result = new ArrayList<>();
            // 普通文件上传
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            multipartRequest.setCharacterEncoding("UTF-8");
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            for (Map.Entry<String, MultipartFile> fileInput : fileMap.entrySet()) {
                // 循环处理待上传列表
                String fileInputName = fileInput.getKey();
                List<MultipartFile> fileList = multipartRequest.getFiles(fileInputName);
                for (MultipartFile multipartFile : fileList) {
                    // 文件头hash
                    byte[] fileBytes = multipartFile.getBytes();
                    byte[] bytes = new byte[10];
                    for (int i = 0; i < bytes.length; i++) {
                        if (fileBytes.length == 0 || fileBytes.length <= i) {
                            break;
                        }
                        bytes[i] = fileBytes[i];
                    }
                    // 文件类型
                    String fileType = FileTypeUtils.getFileType(fileBytes);
                    if (StringUtils.hasText(fileType)) {
                        result.add(FileTypeUtils.getFileHeader(bytes) + "|" + fileType);
                    } else {
                        result.add(FileTypeUtils.getFileHeader(bytes));
                    }
                }
            }
            return result.stream().collect(Collectors.joining(","));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "获取文件头失败！请重试");
        }
    }
}
