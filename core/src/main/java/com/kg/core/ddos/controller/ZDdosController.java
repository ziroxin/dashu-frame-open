package com.kg.core.ddos.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.ddos.dto.ZDdosDTO;
import com.kg.core.ddos.dto.convert.ZDdosConvert;
import com.kg.core.ddos.service.ZDdosService;
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
 * ddos用户请求记录 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2024-07-15
 */
@RestController
@RequestMapping("/ddos/zDdos")
@Api(tags = "ZDdosController", value = "ddos用户请求记录", description = "ddos用户请求记录")
public class ZDdosController {

    @Resource
    private ZDdosService zDdosService;
    @Resource
    private ZDdosConvert zDdosConvert;

    @ApiOperation(value = "/ddos/zDdos/getById", notes = "详情-ddos用户请求记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ddosId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('ddos:zDdos:getById')")
    public ZDdosDTO getById(String ddosId) {
        return zDdosConvert.entityToDto(zDdosService.getById(ddosId));
    }

    @ApiOperation(value = "/ddos/zDdos/list", notes = "分页列表-ddos用户请求记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ddos:zDdos:list')")
    public Page<ZDdosDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return zDdosService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/ddos/zDdos/add", notes = "新增-ddos用户请求记录", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ddos:zDdos:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/ddos/zDdos/add", logMsg = "新增-ddos用户请求记录")
    public void add(@RequestBody ZDdosDTO zDdosDTO) throws BaseException {
        try {
            zDdosService.add(zDdosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/ddos/zDdos/update", notes = "修改-ddos用户请求记录", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ddos:zDdos:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/ddos/zDdos/update", logMsg = "修改-ddos用户请求记录")
    public void update(@RequestBody ZDdosDTO zDdosDTO) throws BaseException {
        try {
            zDdosService.update(zDdosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/ddos/zDdos/delete", notes = "删除-ddos用户请求记录", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ddosIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ddos:zDdos:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/ddos/zDdos/delete", logMsg = "删除-ddos用户请求记录")
    public void delete(@RequestBody String[] ddosIds) throws BaseException {
        try {
            zDdosService.delete(Arrays.asList(ddosIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/ddos/zDdos/export/excel", notes = "导出excel-ddos用户请求记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('ddos:zDdos:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zDdosService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/ddos/zDdos/import/excel", notes = "导入excel-ddos用户请求记录", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('ddos:zDdos:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/ddos/zDdos/import/excel", logMsg = "导入excel-ddos用户请求记录")
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zDdosService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
