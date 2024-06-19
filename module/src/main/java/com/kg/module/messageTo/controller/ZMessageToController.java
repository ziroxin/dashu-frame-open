package com.kg.module.messageTo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.messageTo.dto.ZMessageToDTO;
import com.kg.module.messageTo.dto.convert.ZMessageToConvert;
import com.kg.module.messageTo.service.ZMessageToService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * <p>
 * 消息发送至 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@RestController
@RequestMapping("/messageTo/zMessageTo")
@Api(tags = "ZMessageToController", value = "消息发送至", description = "消息发送至")
public class ZMessageToController {

    @Resource
    private ZMessageToService zMessageToService;
    @Resource
    private ZMessageToConvert zMessageToConvert;

    @ApiOperation(value = "/messageTo/zMessageTo/getById", notes = "详情-消息发送至", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    public ZMessageToDTO getById(String toId) {
        return zMessageToConvert.entityToDto(zMessageToService.getById(toId));
    }

    @ApiOperation(value = "/messageTo/zMessageTo/list", notes = "分页列表-消息发送至", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    public Page<ZMessageToDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return zMessageToService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/messageTo/zMessageTo/add", notes = "新增-消息发送至", httpMethod = "POST")
    @PostMapping("/add")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/messageTo/zMessageTo/add", logMsg = "新增-消息发送至")
    public void add(@RequestBody ZMessageToDTO zMessageToDTO) throws BaseException {
        try {
            zMessageToService.add(zMessageToDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/messageTo/zMessageTo/update", notes = "修改-消息发送至", httpMethod = "PUT")
    @PutMapping("/update")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/messageTo/zMessageTo/update", logMsg = "修改-消息发送至")
    public void update(@RequestBody ZMessageToDTO zMessageToDTO) throws BaseException {
        try {
            zMessageToService.update(zMessageToDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/messageTo/zMessageTo/delete", notes = "删除-消息发送至", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/messageTo/zMessageTo/delete", logMsg = "删除-消息发送至")
    public void delete(@RequestBody String[] toIds) throws BaseException {
        try {
            zMessageToService.delete(Arrays.asList(toIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/messageTo/zMessageTo/export/excel", notes = "导出excel-消息发送至", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zMessageToService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/messageTo/zMessageTo/import/excel", notes = "导入excel-消息发送至", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/messageTo/zMessageTo/import/excel", logMsg = "导入excel-消息发送至")
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zMessageToService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
