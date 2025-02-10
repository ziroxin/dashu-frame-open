package com.kg.module.filesStatic.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.file.dto.FileDTO;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
            if (zFilesStaticService.lambdaQuery()
                    .eq(ZFilesStatic::getFileOldName, zFilesStaticDTO.getFileOldName()).count() > 0) {
                throw new BaseException("文件夹名已存在！请修改");
            }
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
            if (zFilesStaticService.lambdaQuery().ne(ZFilesStatic::getFileId, zFilesStaticDTO.getFileId())
                    .eq(ZFilesStatic::getFileOldName, zFilesStaticDTO.getFileOldName()).count() > 0) {
                throw new BaseException("文件夹名已存在！请修改");
            }
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
            List<String> delIdList = Arrays.asList(fileIds);
            if (zFilesStaticService.lambdaQuery().in(ZFilesStatic::getParentId, delIdList).count() > 0) {
                throw new BaseException("删除失败！文件夹中已有文件，请先删除文件");
            }
            zFilesStaticService.delete(delIdList);
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
            @ApiImplicitParam(name = "parentId", value = "所属文件夹id", paramType = "query", required = false, dataType = "String")
    })
    @PostMapping("upload")
    public List<FileDTO> upload(HttpServletRequest request, String parentId) throws BaseException {
        try {
            // 查重
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Iterator<String> fileNamesIterator = multipartRequest.getFileNames();
            int count = 0;
            while (fileNamesIterator.hasNext()) {
                // 获取文件的实际名称
                String fileOriginalName = multipartRequest.getFile(fileNamesIterator.next()).getOriginalFilename();
                // 检查是否有同名文件已经存在
                if (zFilesStaticService.lambdaQuery().eq(ZFilesStatic::getParentId, parentId)
                        .eq(ZFilesStatic::getFileOldName, fileOriginalName).count() > 0) {
                    throw new BaseException("上传失败！同名文件已存在: " + fileOriginalName);
                }
                count++;
            }
            if (count <= 0) {
                throw new BaseException("上传失败！未选择文件");
            }

            // 普通文件上传
            ZFilesStatic entity = zFilesStaticService.getById(parentId);
            List<FileDTO> fileDTOS = UploadFileUtils.uploadOriginal(request, entity.getFileOldName());
            // 保存到数据库
            List<ZFilesStatic> saveList = fileDTOS.stream().map(dto -> {
                ZFilesStatic bean = JSONUtil.toBean(JSONUtil.toJsonStr(dto), ZFilesStatic.class);
                bean.setFileId(GuidUtils.getUuid());
                bean.setParentId(parentId);
                bean.setFileType("1");// 文件类型：1文件
                bean.setCreateTime(LocalDateTime.now());
                return bean;
            }).collect(Collectors.toList());
            zFilesStaticService.saveBatch(saveList);
            return fileDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "上传文件失败！请重试");
        }
    }
}
