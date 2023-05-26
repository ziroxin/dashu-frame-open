package com.kg.module.dictType.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.dictType.dto.ZDictTypeDTO;
import com.kg.module.dictType.dto.convert.ZDictTypeConvert;
import com.kg.module.dictType.service.ZDictTypeService;
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
 * 字典类型 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@RestController
@RequestMapping("/dictType/zDictType")
@Api(tags = "ZDictTypeController", value = "字典类型", description = "字典类型")
public class ZDictTypeController {

    @Resource
    private ZDictTypeService zDictTypeService;
    @Resource
    private ZDictTypeConvert zDictTypeConvert;

    @ApiOperation(value = "/dictType/zDictType/getById", notes = "详情-字典类型", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('dictType:zDictType:getById')")
    public ZDictTypeDTO getById(String typeId) {
        return zDictTypeConvert.entityToDto(zDictTypeService.getById(typeId));
    }

    @ApiOperation(value = "/dictType/zDictType/list", notes = "分页列表-字典类型", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('dictType:zDictType:list')")
    public Page<ZDictTypeDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return zDictTypeService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/dictType/zDictType/add", notes = "新增-字典类型", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('dictType:zDictType:add')")
    @NoRepeatSubmit
    public void add(@RequestBody ZDictTypeDTO zDictTypeDTO) throws BaseException {
        try {
            zDictTypeService.add(zDictTypeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/dictType/zDictType/update", notes = "修改-字典类型", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('dictType:zDictType:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ZDictTypeDTO zDictTypeDTO) throws BaseException {
        try {
            zDictTypeService.update(zDictTypeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/dictType/zDictType/delete", notes = "删除-字典类型", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('dictType:zDictType:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] typeIds) throws BaseException {
        try {
            zDictTypeService.delete(Arrays.asList(typeIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/dictType/zDictType/export/excel", notes = "导出excel-字典类型", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('dictType:zDictType:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zDictTypeService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/dictType/zDictType/import/excel", notes = "导入excel-字典类型", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('dictType:zDictType:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zDictTypeService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
