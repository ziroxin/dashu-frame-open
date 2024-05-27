package com.kg.module.config.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.config.dto.ZConfigDTO;
import com.kg.module.config.dto.convert.ZConfigConvert;
import com.kg.module.config.service.ZConfigService;
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
 * 参数参数配置 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2024-05-27
 */
@RestController
@RequestMapping("/config/zConfig")
@Api(tags = "ZConfigController", value = "参数参数配置", description = "参数参数配置")
public class ZConfigController {

    @Resource
    private ZConfigService zConfigService;
    @Resource
    private ZConfigConvert zConfigConvert;

    @ApiOperation(value = "/config/zConfig/getById", notes = "详情-参数参数配置", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfgId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('config:zConfig:getById')")
    public ZConfigDTO getById(String cfgId) {
        return zConfigConvert.entityToDto(zConfigService.getById(cfgId));
    }

    @ApiOperation(value = "/config/zConfig/list", notes = "分页列表-参数参数配置", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('config:zConfig:list')")
    public Page<ZConfigDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return zConfigService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/config/zConfig/add", notes = "新增-参数参数配置", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('config:zConfig:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/config/zConfig/add", logMsg = "新增-参数参数配置")
    public void add(@RequestBody ZConfigDTO zConfigDTO) throws BaseException {
        try {
            zConfigService.add(zConfigDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/config/zConfig/update", notes = "修改-参数参数配置", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('config:zConfig:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/config/zConfig/update", logMsg = "修改-参数参数配置")
    public void update(@RequestBody ZConfigDTO zConfigDTO) throws BaseException {
        try {
            zConfigService.update(zConfigDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/config/zConfig/delete", notes = "删除-参数参数配置", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfgIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('config:zConfig:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/config/zConfig/delete", logMsg = "删除-参数参数配置")
    public void delete(@RequestBody String[] cfgIds) throws BaseException {
        try {
            zConfigService.delete(Arrays.asList(cfgIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/config/zConfig/export/excel", notes = "导出excel-参数参数配置", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('config:zConfig:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zConfigService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/config/zConfig/import/excel", notes = "导入excel-参数参数配置", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('config:zConfig:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/config/zConfig/import/excel", logMsg = "导入excel-参数参数配置")
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zConfigService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
