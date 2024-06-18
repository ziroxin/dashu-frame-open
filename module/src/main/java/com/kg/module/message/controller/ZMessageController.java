package com.kg.module.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.message.dto.ZMessageDTO;
import com.kg.module.message.dto.convert.ZMessageConvert;
import com.kg.module.message.service.ZMessageService;
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
 * 消息中心 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@RestController
@RequestMapping("/message/zMessage")
@Api(tags = "ZMessageController", value = "消息中心", description = "消息中心")
public class ZMessageController {

    @Resource
    private ZMessageService zMessageService;
    @Resource
    private ZMessageConvert zMessageConvert;

    @ApiOperation(value = "/message/zMessage/getById", notes = "详情-消息中心", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('message:zMessage:getById')")
    public ZMessageDTO getById(String msgId) {
        return zMessageConvert.entityToDto(zMessageService.getById(msgId));
    }

    @ApiOperation(value = "/message/zMessage/list", notes = "分页列表-消息中心", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('message:zMessage:list')")
    public Page<ZMessageDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return zMessageService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/message/zMessage/add", notes = "新增-消息中心", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('message:zMessage:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/message/zMessage/add", logMsg = "新增-消息中心")
    public void add(@RequestBody ZMessageDTO zMessageDTO) throws BaseException {
        try {
            zMessageService.add(zMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/message/zMessage/update", notes = "修改-消息中心", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('message:zMessage:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/message/zMessage/update", logMsg = "修改-消息中心")
    public void update(@RequestBody ZMessageDTO zMessageDTO) throws BaseException {
        try {
            zMessageService.update(zMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/message/zMessage/delete", notes = "删除-消息中心", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('message:zMessage:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/message/zMessage/delete", logMsg = "删除-消息中心")
    public void delete(@RequestBody String[] msgIds) throws BaseException {
        try {
            zMessageService.delete(Arrays.asList(msgIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/message/zMessage/export/excel", notes = "导出excel-消息中心", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('message:zMessage:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zMessageService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/message/zMessage/import/excel", notes = "导入excel-消息中心", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('message:zMessage:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/message/zMessage/import/excel", logMsg = "导入excel-消息中心")
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zMessageService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
