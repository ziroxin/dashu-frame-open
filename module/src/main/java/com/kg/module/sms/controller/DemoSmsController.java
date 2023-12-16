package com.kg.module.sms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.sms.dto.DemoSmsDTO;
import com.kg.module.sms.dto.convert.DemoSmsConvert;
import com.kg.module.sms.service.DemoSmsService;
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
 * 短信 - demo 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-12-14
 */
@RestController
@RequestMapping("/sms/demoSms")
@Api(tags = "DemoSmsController", value = "短信 - demo", description = "短信 - demo")
public class DemoSmsController {

    @Resource
    private DemoSmsService demoSmsService;
    @Resource
    private DemoSmsConvert demoSmsConvert;

    @ApiOperation(value = "/sms/demoSms/getById", notes = "详情-短信 - demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "smsId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('sms:demoSms:getById')")
    public DemoSmsDTO getById(String smsId) {
        return demoSmsConvert.entityToDto(demoSmsService.getById(smsId));
    }

    @ApiOperation(value = "/sms/demoSms/list", notes = "分页列表-短信 - demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:demoSms:list')")
    public Page<DemoSmsDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) throws BaseException {
        return demoSmsService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/sms/demoSms/add", notes = "新增-短信 - demo", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sms:demoSms:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/sms/demoSms/add", logMsg = "新增-短信 - demo")
    public void add(@RequestBody DemoSmsDTO demoSmsDTO) throws BaseException {
        try {
            demoSmsService.add(demoSmsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/sms/demoSms/update", notes = "修改-短信 - demo", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sms:demoSms:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/sms/demoSms/update", logMsg = "修改-短信 - demo")
    public void update(@RequestBody DemoSmsDTO demoSmsDTO) throws BaseException {
        try {
            demoSmsService.update(demoSmsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/sms/demoSms/delete", notes = "删除-短信 - demo", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "smsIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('sms:demoSms:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/sms/demoSms/delete", logMsg = "删除-短信 - demo")
    public void delete(@RequestBody String[] smsIds) throws BaseException {
        try {
            demoSmsService.delete(Arrays.asList(smsIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/sms/demoSms/export/excel", notes = "导出excel-短信 - demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('sms:demoSms:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = demoSmsService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/sms/demoSms/import/excel", notes = "导入excel-短信 - demo", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('sms:demoSms:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/sms/demoSms/import/excel", logMsg = "导入excel-短信 - demo")
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            demoSmsService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
