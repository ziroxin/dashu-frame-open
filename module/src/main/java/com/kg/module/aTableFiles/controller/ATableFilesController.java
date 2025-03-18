package com.kg.module.aTableFiles.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.web.ResponseResult;
import com.kg.module.aTableFiles.dto.ATableFilesDTO;
import com.kg.module.aTableFiles.dto.convert.ATableFilesConvert;
import com.kg.module.aTableFiles.service.ATableFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * <p>
 * 我的表a_table附件表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@RestController
@RequestMapping("/aTableFiles/aTableFiles")
@Api(tags = "ATableFilesController", value = "我的表a_table附件表", description = "我的表a_table附件表")
public class ATableFilesController {

    @Resource
    private ATableFilesService aTableFilesService;
    @Resource
    private ATableFilesConvert aTableFilesConvert;

    @ApiOperation(value = "/aTableFiles/aTableFiles/getById", notes = "详情-我的表a_table附件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('aTableFiles:aTableFiles:getById')")
    public ATableFilesDTO getById(String fileId) {
        return aTableFilesConvert.entityToDto(aTableFilesService.getById(fileId));
    }

    @ApiOperation(value = "/aTableFiles/aTableFiles/list", notes = "分页列表-我的表a_table附件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sorts", value = "排序条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('aTableFiles:aTableFiles:list')")
    public Page<ATableFilesDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                @RequestParam(value = "params", required = false) String params,
                                @RequestParam(value = "sorts", required = false) String sorts) {
        return aTableFilesService.pagelist(page, limit, params, sorts);
    }

    @ApiOperation(value = "/aTableFiles/aTableFiles/add", notes = "新增-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('aTableFiles:aTableFiles:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableFiles/aTableFiles/add", logMsg = "新增-我的表a_table附件表")
    public void add(@RequestBody ATableFilesDTO aTableFilesDTO) throws BaseException {
        try {
            aTableFilesService.add(aTableFilesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/aTableFiles/aTableFiles/update", notes = "修改-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('aTableFiles:aTableFiles:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableFiles/aTableFiles/update", logMsg = "修改-我的表a_table附件表")
    public void update(@RequestBody ATableFilesDTO aTableFilesDTO) throws BaseException {
        try {
            aTableFilesService.update(aTableFilesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/aTableFiles/aTableFiles/delete", notes = "删除-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('aTableFiles:aTableFiles:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableFiles/aTableFiles/delete", logMsg = "删除-我的表a_table附件表")
    public void delete(@RequestBody String[] fileIds) throws BaseException {
        try {
            aTableFilesService.delete(Arrays.asList(fileIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/aTableFiles/aTableFiles/export/excel", notes = "导出excel-我的表a_table附件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sorts", value = "排序条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('aTableFiles:aTableFiles:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params,
                              @RequestParam(value = "sorts", required = false) String sorts) throws BaseException {
        String result = aTableFilesService.exportExcel(params, sorts);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/aTableFiles/aTableFiles/import/excel", notes = "导入excel-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('aTableFiles:aTableFiles:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableFiles/aTableFiles/import/excel", logMsg = "导入excel-我的表a_table附件表")
    public ResponseResult importExcel(HttpServletRequest request) throws BaseException {
        try {
            String result = aTableFilesService.importExcel(request);
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

    @ApiOperation(value = "/aTableFiles/aTableFiles/import/downloadTemplate", notes = "下载导入模板-我的表a_table附件表", httpMethod = "GET")
    @GetMapping("/import/downloadTemplate")
    public String downloadTemplate() throws BaseException {
        String result = aTableFilesService.downloadTemplate();
        if ("error".equals(result)) {
            throw new BaseException("下载导入模板失败，请重试！");
        }
        return result;
    }
}
