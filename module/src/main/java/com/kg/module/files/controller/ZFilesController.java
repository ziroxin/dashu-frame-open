package com.kg.module.files.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.files.dto.ZFilesDTO;
import com.kg.module.files.dto.convert.ZFilesConvert;
import com.kg.module.files.service.ZFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * <p>
 * 文件记录表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-09-15
 */
@RestController
@RequestMapping("/files/zFiles")
@Api(tags = "ZFilesController", value = "文件记录表", description = "文件记录表")
public class ZFilesController {

    @Resource
    private ZFilesService zFilesService;
    @Resource
    private ZFilesConvert zFilesConvert;

    @ApiOperation(value = "/files/zFiles/getById", notes = "详情-文件记录表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('files:zFiles:getById')")
    public ZFilesDTO getById(String fileId) {
        return zFilesConvert.entityToDto(zFilesService.getById(fileId));
    }

    @ApiOperation(value = "/files/zFiles/list", notes = "分页列表-文件记录表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('files:zFiles:list')")
    public Page<ZFilesDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return zFilesService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/files/zFiles/add", notes = "新增-文件记录表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('files:zFiles:add')")
    @NoRepeatSubmit
    public void add(@RequestBody ZFilesDTO zFilesDTO) throws BaseException {
        try {
            zFilesService.add(zFilesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/files/zFiles/update", notes = "修改-文件记录表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('files:zFiles:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ZFilesDTO zFilesDTO) throws BaseException {
        try {
            zFilesService.update(zFilesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/files/zFiles/delete", notes = "删除-文件记录表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('files:zFiles:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] fileIds) throws BaseException {
        try {
            zFilesService.delete(Arrays.asList(fileIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/files/zFiles/export/excel", notes = "导出excel-文件记录表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('files:zFiles:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zFilesService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/files/zFiles/import/excel", notes = "导入excel-文件记录表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('files:zFiles:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zFilesService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
