package com.kg.module.dictData.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.dictData.dto.ZDictDataDTO;
import com.kg.module.dictData.dto.convert.ZDictDataConvert;
import com.kg.module.dictData.entity.ZDictData;
import com.kg.module.dictData.service.ZDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 字典数据 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@RestController
@RequestMapping("/dictData/zDictData")
@Api(tags = "ZDictDataController", value = "字典数据", description = "字典数据")
public class ZDictDataController {

    @Resource
    private ZDictDataService zDictDataService;
    @Resource
    private ZDictDataConvert zDictDataConvert;

    @ApiOperation(value = "/dictData/zDictData/listCache", notes = "获取字典数据（缓存redis）", httpMethod = "GET")
    @GetMapping("/listCache")
    public List<ZDictData> listCache(String typeCode) {
        return zDictDataService.listCache(typeCode);
    }

    @ApiOperation(value = "/dictData/zDictData/clearCache", notes = "清空数据字典缓存数据", httpMethod = "GET")
    @GetMapping("/clearCache")
    public void clearCache(String typeCode) {
        zDictDataService.clearCache(typeCode);
    }

    @ApiOperation(value = "/dictData/zDictData/getById", notes = "详情-字典数据", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('dictData:zDictData:getById')")
    public ZDictDataDTO getById(String dictId) {
        return zDictDataConvert.entityToDto(zDictDataService.getById(dictId));
    }

    @ApiOperation(value = "/dictData/zDictData/list", notes = "分页列表-字典数据", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('dictData:zDictData:list')")
    public Page<ZDictDataDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                   @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                   @RequestParam(value = "params", required = false) String params) {
        return zDictDataService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/dictData/zDictData/add", notes = "新增-字典数据", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('dictData:zDictData:add')")
    @NoRepeatSubmit
    public void add(@RequestBody ZDictDataDTO zDictDataDTO) throws BaseException {
        try {
            zDictDataService.add(zDictDataDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/dictData/zDictData/update", notes = "修改-字典数据", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('dictData:zDictData:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ZDictDataDTO zDictDataDTO) throws BaseException {
        try {
            zDictDataService.update(zDictDataDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/dictData/zDictData/delete", notes = "删除-字典数据", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('dictData:zDictData:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] dictIds) throws BaseException {
        try {
            zDictDataService.delete(Arrays.asList(dictIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/dictData/zDictData/export/excel", notes = "导出excel-字典数据", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('dictData:zDictData:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zDictDataService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/dictData/zDictData/import/excel", notes = "导入excel-字典数据", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('dictData:zDictData:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zDictDataService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
