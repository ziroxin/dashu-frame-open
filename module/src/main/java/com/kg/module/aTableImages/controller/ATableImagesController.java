package com.kg.module.aTableImages.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.web.ResponseResult;
import com.kg.module.aTableImages.dto.ATableImagesDTO;
import com.kg.module.aTableImages.dto.convert.ATableImagesConvert;
import com.kg.module.aTableImages.service.ATableImagesService;
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
@RequestMapping("/aTableImages/aTableImages")
@Api(tags = "ATableImagesController", value = "我的表a_table附件表", description = "我的表a_table附件表")
public class ATableImagesController {

    @Resource
    private ATableImagesService aTableImagesService;
    @Resource
    private ATableImagesConvert aTableImagesConvert;

    @ApiOperation(value = "/aTableImages/aTableImages/getById", notes = "详情-我的表a_table附件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('aTableImages:aTableImages:getById')")
    public ATableImagesDTO getById(String fileId) {
        return aTableImagesConvert.entityToDto(aTableImagesService.getById(fileId));
    }

    @ApiOperation(value = "/aTableImages/aTableImages/list", notes = "分页列表-我的表a_table附件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sorts", value = "排序条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('aTableImages:aTableImages:list')")
    public Page<ATableImagesDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                @RequestParam(value = "params", required = false) String params,
                                @RequestParam(value = "sorts", required = false) String sorts) {
        return aTableImagesService.pagelist(page, limit, params, sorts);
    }

    @ApiOperation(value = "/aTableImages/aTableImages/add", notes = "新增-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('aTableImages:aTableImages:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableImages/aTableImages/add", logMsg = "新增-我的表a_table附件表")
    public void add(@RequestBody ATableImagesDTO aTableImagesDTO) throws BaseException {
        try {
            aTableImagesService.add(aTableImagesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/aTableImages/aTableImages/update", notes = "修改-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('aTableImages:aTableImages:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableImages/aTableImages/update", logMsg = "修改-我的表a_table附件表")
    public void update(@RequestBody ATableImagesDTO aTableImagesDTO) throws BaseException {
        try {
            aTableImagesService.update(aTableImagesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/aTableImages/aTableImages/delete", notes = "删除-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('aTableImages:aTableImages:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableImages/aTableImages/delete", logMsg = "删除-我的表a_table附件表")
    public void delete(@RequestBody String[] fileIds) throws BaseException {
        try {
            aTableImagesService.delete(Arrays.asList(fileIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/aTableImages/aTableImages/export/excel", notes = "导出excel-我的表a_table附件表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sorts", value = "排序条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('aTableImages:aTableImages:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params,
                              @RequestParam(value = "sorts", required = false) String sorts) throws BaseException {
        String result = aTableImagesService.exportExcel(params, sorts);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/aTableImages/aTableImages/import/excel", notes = "导入excel-我的表a_table附件表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('aTableImages:aTableImages:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/aTableImages/aTableImages/import/excel", logMsg = "导入excel-我的表a_table附件表")
    public ResponseResult importExcel(HttpServletRequest request) throws BaseException {
        try {
            String result = aTableImagesService.importExcel(request);
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

    @ApiOperation(value = "/aTableImages/aTableImages/import/downloadTemplate", notes = "下载导入模板-我的表a_table附件表", httpMethod = "GET")
    @GetMapping("/import/downloadTemplate")
    public String downloadTemplate() throws BaseException {
        String result = aTableImagesService.downloadTemplate();
        if ("error".equals(result)) {
            throw new BaseException("下载导入模板失败，请重试！");
        }
        return result;
    }
}
