package com.kg.module.atable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.web.ResponseResult;
import com.kg.module.atable.dto.ATableDTO;
import com.kg.module.atable.dto.convert.ATableConvert;
import com.kg.module.atable.service.ATableService;
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
 * 我的表a_table 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@RestController
@RequestMapping("/atable/aTable")
@Api(tags = "ATableController", value = "我的表a_table", description = "我的表a_table")
public class ATableController {

    @Resource
    private ATableService aTableService;
    @Resource
    private ATableConvert aTableConvert;

    @ApiOperation(value = "/atable/aTable/getById", notes = "详情-我的表a_table", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('atable:aTable:getById')")
    public ATableDTO getById(String id) {
        return aTableConvert.entityToDto(aTableService.getById(id));
    }

    @ApiOperation(value = "/atable/aTable/list", notes = "分页列表-我的表a_table", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sorts", value = "排序条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('atable:aTable:list')")
    public Page<ATableDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                @RequestParam(value = "params", required = false) String params,
                                @RequestParam(value = "sorts", required = false) String sorts) {
        return aTableService.pagelist(page, limit, params, sorts);
    }

    @ApiOperation(value = "/atable/aTable/add", notes = "新增-我的表a_table", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('atable:aTable:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/atable/aTable/add", logMsg = "新增-我的表a_table")
    public void add(@RequestBody ATableDTO aTableDTO) throws BaseException {
        try {
            aTableService.add(aTableDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/atable/aTable/update", notes = "修改-我的表a_table", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('atable:aTable:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/atable/aTable/update", logMsg = "修改-我的表a_table")
    public void update(@RequestBody ATableDTO aTableDTO) throws BaseException {
        try {
            aTableService.update(aTableDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/atable/aTable/delete", notes = "删除-我的表a_table", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('atable:aTable:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/atable/aTable/delete", logMsg = "删除-我的表a_table")
    public void delete(@RequestBody String[] ids) throws BaseException {
        try {
            aTableService.delete(Arrays.asList(ids));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/atable/aTable/export/excel", notes = "导出excel-我的表a_table", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "sorts", value = "排序条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('atable:aTable:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params,
                              @RequestParam(value = "sorts", required = false) String sorts) throws BaseException {
        String result = aTableService.exportExcel(params, sorts);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/atable/aTable/import/excel", notes = "导入excel-我的表a_table", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('atable:aTable:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/atable/aTable/import/excel", logMsg = "导入excel-我的表a_table")
    public ResponseResult importExcel(HttpServletRequest request) throws BaseException {
        try {
            String result = aTableService.importExcel(request);
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

    @ApiOperation(value = "/atable/aTable/import/downloadTemplate", notes = "下载导入模板-我的表a_table", httpMethod = "GET")
    @GetMapping("/import/downloadTemplate")
    public String downloadTemplate() throws BaseException {
        String result = aTableService.downloadTemplate();
        if ("error".equals(result)) {
            throw new BaseException("下载导入模板失败，请重试！");
        }
        return result;
    }
}
