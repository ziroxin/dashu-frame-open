package com.kg.module.filesStatic.controller;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileDTO;
import com.kg.component.file.utils.RemoveFileUtils;
import com.kg.component.file.utils.UploadFileUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.web.ResponseResult;
import com.kg.module.filesStatic.dto.ZFilesStaticDTO;
import com.kg.module.filesStatic.dto.convert.ZFilesStaticConvert;
import com.kg.module.filesStatic.entity.ZFilesStatic;
import com.kg.module.filesStatic.service.ZFilesStaticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

/**
 * <p>
 * 静态资源文件表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2025-01-24
 */
@RestController
@RequestMapping("/filesStatic/zFilesStatic")
@Api(tags = "ZFilesStaticController", value = "静态资源文件表", description = "静态资源文件表")
public class ZFilesStaticController {

    @Resource
    private ZFilesStaticService zFilesStaticService;
    @Resource
    private ZFilesStaticConvert zFilesStaticConvert;

    @ApiOperation(value = "/filesStatic/zFilesStatic/getById", notes = "详情-静态资源文件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('filesStatic:zFilesStatic:getById')")
    public ZFilesStaticDTO getById(String fileId) {
        return zFilesStaticConvert.entityToDto(zFilesStaticService.getById(fileId));
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/list", notes = "分页列表-静态资源文件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('filesStatic:zFilesStatic:list')")
    public Page<ZFilesStaticDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                      @RequestParam(value = "params", required = false) String params) {
        return zFilesStaticService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/add", notes = "新增-静态资源文件表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('filesStatic:zFilesStatic:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/filesStatic/zFilesStatic/add", logMsg = "新增-静态资源文件表")
    public void add(@RequestBody ZFilesStaticDTO zFilesStaticDTO) throws BaseException {
        try {
            zFilesStaticService.add(zFilesStaticDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/update", notes = "修改-静态资源文件表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('filesStatic:zFilesStatic:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/filesStatic/zFilesStatic/update", logMsg = "修改-静态资源文件表")
    public void update(@RequestBody ZFilesStaticDTO zFilesStaticDTO) throws BaseException {
        try {
            zFilesStaticService.update(zFilesStaticDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/delete", notes = "删除-静态资源文件表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('filesStatic:zFilesStatic:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/filesStatic/zFilesStatic/delete", logMsg = "删除-静态资源文件表")
    public void delete(@RequestBody String[] fileIds) throws BaseException {
        try {
            zFilesStaticService.delete(Arrays.asList(fileIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/export/excel", notes = "导出excel-静态资源文件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('filesStatic:zFilesStatic:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zFilesStaticService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/import/excel", notes = "导入excel-静态资源文件表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('filesStatic:zFilesStatic:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/filesStatic/zFilesStatic/import/excel", logMsg = "导入excel-静态资源文件表")
    public ResponseResult importExcel(HttpServletRequest request) throws BaseException {
        try {
            String result = zFilesStaticService.importExcel(request);
            if (StringUtils.hasText(result)) {
                // 导入失败，返回错误提示信息
                return ResponseResult.builder().code("200").message(result).build();
            } else {
                return ResponseResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "导入Excel失败，请重试！");
        }
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/import/downloadTemplate", notes = "下载导入模板-静态资源文件表", httpMethod = "GET")
    @GetMapping("/import/downloadTemplate")
    public String downloadTemplate() throws BaseException {
        String result = zFilesStaticService.downloadTemplate();
        if ("error".equals(result)) {
            throw new BaseException("下载导入模板失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/filesStatic/zFilesStatic/upload", notes = "上传文件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "autoUnzip", value = "是否自动解压", paramType = "query", required = false, dataType = "boolean"),
            @ApiImplicitParam(name = "parentId", value = "所属文件夹id", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("upload")
    public void upload(HttpServletRequest request, boolean autoUnzip, String parentId) throws BaseException {
        try {
            ZFilesStatic entity = zFilesStaticService.getById(parentId);
            // 普通文件上传
            List<FileDTO> fileDTOS = UploadFileUtils.uploadOriginal(request, entity.getFileOldName());
            // 自动解压处理
            if (autoUnzip && fileDTOS != null && fileDTOS.size() > 0 && fileDTOS.get(0).getFileExtend().equals("zip")) {
                // 自动解压文件
                String savePath = FilePathConfig.switchSavePath(fileDTOS.get(0).getFileUrl());
                ZipUtil.unzip(savePath);
                // 解压后的文件保存到数据库
                List<String> list = ZipUtil.listFileNames(new ZipFile(savePath), null);
                String parentPath = FilePathConfig.SAVE_PATH + "/" + entity.getFileOldName() + "/";
                List<ZFilesStatic> saveList = new ArrayList<>();
                for (String fileName : list) {
                    ZFilesStatic bean = new ZFilesStatic();
                    bean.setFileId(GuidUtils.getUuid());
                    bean.setParentId(parentId);
                    bean.setFileType("1");// 文件类型：1文件
                    bean.setFileOldName(fileName);
                    bean.setFileName(fileName);
                    bean.setFileExtend(fileName.substring(fileName.lastIndexOf(".") + 1));
                    bean.setFileUrl(FilePathConfig.switchUrl(parentPath + fileName));
                    bean.setCreateTime(LocalDateTime.now());
                    saveList.add(bean);
                }
                zFilesStaticService.saveBatch(saveList);
                // 删除压缩包
                RemoveFileUtils.remove(fileDTOS.get(0).getFileUrl());
            } else {
                // 保存当前这一个文件
                List<ZFilesStatic> saveList = fileDTOS.stream().map(dto -> {
                    ZFilesStatic bean = JSONUtil.toBean(JSONUtil.toJsonStr(dto), ZFilesStatic.class);
                    bean.setFileId(GuidUtils.getUuid());
                    bean.setParentId(parentId);
                    bean.setFileType("1");// 文件类型：1文件
                    bean.setCreateTime(LocalDateTime.now());
                    return bean;
                }).collect(Collectors.toList());
                zFilesStaticService.saveBatch(saveList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "上传文件失败！请重试");
        }
    }
}
