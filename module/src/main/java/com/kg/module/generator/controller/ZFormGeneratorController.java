package com.kg.module.generator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.generator.dto.ZFormGeneratorDTO;
import com.kg.module.generator.dto.convert.ZFormGeneratorConvert;
import com.kg.module.generator.service.ZFormGeneratorService;
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
 * 代码生成器表单 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-11-22
 */
@RestController
@RequestMapping("/generator/zFormGenerator")
@Api(tags = "ZFormGeneratorController", value = "代码生成器表单", description = "代码生成器表单")
public class ZFormGeneratorController {

    @Resource
    private ZFormGeneratorService zFormGeneratorService;
    @Resource
    private ZFormGeneratorConvert zFormGeneratorConvert;

    @ApiOperation(value = "/generator/zFormGenerator/getById", notes = "详情-代码生成器表单", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('generator:zFormGenerator:getById')")
    public ZFormGeneratorDTO getById(String formId) {
        return zFormGeneratorConvert.entityToDto(zFormGeneratorService.getById(formId));
    }

    @ApiOperation(value = "/generator/zFormGenerator/list", notes = "分页列表-代码生成器表单", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('generator:zFormGenerator:list')")
    public Page<ZFormGeneratorDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return zFormGeneratorService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/generator/zFormGenerator/add", notes = "新增-代码生成器表单", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('generator:zFormGenerator:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/generator/zFormGenerator/add", logMsg = "新增-代码生成器表单")
    public void add(@RequestBody ZFormGeneratorDTO zFormGeneratorDTO) throws BaseException {
        try {
            zFormGeneratorService.add(zFormGeneratorDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/generator/zFormGenerator/update", notes = "修改-代码生成器表单", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('generator:zFormGenerator:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/generator/zFormGenerator/update", logMsg = "修改-代码生成器表单")
    public void update(@RequestBody ZFormGeneratorDTO zFormGeneratorDTO) throws BaseException {
        try {
            zFormGeneratorService.update(zFormGeneratorDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/generator/zFormGenerator/delete", notes = "删除-代码生成器表单", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('generator:zFormGenerator:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/generator/zFormGenerator/delete", logMsg = "删除-代码生成器表单")
    public void delete(@RequestBody String[] formIds) throws BaseException {
        try {
            zFormGeneratorService.delete(Arrays.asList(formIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/generator/zFormGenerator/export/excel", notes = "导出excel-代码生成器表单", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('generator:zFormGenerator:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zFormGeneratorService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/generator/zFormGenerator/import/excel", notes = "导入excel-代码生成器表单", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('generator:zFormGenerator:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/generator/zFormGenerator/import/excel", logMsg = "导入excel-代码生成器表单")
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zFormGeneratorService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
