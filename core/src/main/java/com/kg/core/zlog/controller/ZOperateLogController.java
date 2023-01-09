package com.kg.core.zlog.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.utils.GuidUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.zlog.dto.ZOperateLogDTO;
import com.kg.core.zlog.dto.convert.ZOperateLogConvert;
import com.kg.core.zlog.entity.ZOperateLog;
import com.kg.core.zlog.service.ZOperateLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 * 操作日志表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/zlog/zOperateLog")
@Api(tags = "ZOperateLogController", value = "操作日志表", description = "操作日志表")
public class ZOperateLogController {

    @Resource
    private ZOperateLogService zOperateLogService;
    @Resource
    private ZOperateLogConvert zOperateLogConvert;

    @ApiOperation(value = "/zlog/zOperateLog/getById", notes = "详情-操作日志表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('zlog:zOperateLog:getById')")
    public ZOperateLogDTO getById(String logId) {
        return zOperateLogConvert.entityToDto(zOperateLogService.getById(logId));
    }

    @ApiOperation(value = "/zlog/zOperateLog/list", notes = "分页列表-操作日志表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('zlog:zOperateLog:list')")
    public Page<ZOperateLog> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                  @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                  @RequestParam(value = "params", required = false) String params) {
        Page<ZOperateLog> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZOperateLog> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("logId")) {
                wrapper.lambda().eq(ZOperateLog::getLogId, paramObj.getStr("logId"));
            }
            if (paramObj.containsKey("userId")) {
                wrapper.lambda().eq(ZOperateLog::getUserId, paramObj.getStr("userId"));
            }
            if (paramObj.containsKey("userName")) {
                wrapper.lambda().like(ZOperateLog::getUserName, paramObj.getStr("userName"));
            }
            if (paramObj.containsKey("logMethod")) {
                wrapper.lambda().like(ZOperateLog::getLogMethod, paramObj.getStr("logMethod"));
            }
            if (paramObj.containsKey("logMsg")) {
                wrapper.lambda().eq(ZOperateLog::getLogMsg, paramObj.getStr("logMsg"));
            }
            if (paramObj.containsKey("content")) {
                wrapper.lambda().eq(ZOperateLog::getContent, paramObj.getStr("content"));
            }
            if (paramObj.containsKey("actionUrl")) {
                wrapper.lambda().eq(ZOperateLog::getActionUrl, paramObj.getStr("actionUrl"));
            }
            if (paramObj.containsKey("ip")) {
                wrapper.lambda().eq(ZOperateLog::getIp, paramObj.getStr("ip"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(ZOperateLog::getCreateTime, paramObj.getStr("createTime"));
            }
        }
        wrapper.lambda().orderByDesc(ZOperateLog::getCreateTime);
        return zOperateLogService.page(pager, wrapper);
    }

    @ApiOperation(value = "/zlog/zOperateLog/add", notes = "新增-操作日志表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('zlog:zOperateLog:add')")
    @NoRepeatSubmit
    public void add(@RequestBody ZOperateLogDTO zOperateLogDTO) throws BaseException {
        try {
            ZOperateLog zOperateLog = zOperateLogConvert.dtoToEntity(zOperateLogDTO);
            zOperateLog.setLogId(GuidUtils.getUuid());
            zOperateLog.setCreateTime(LocalDateTime.now());
            zOperateLogService.save(zOperateLog);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("新增失败！请重试");
        }
    }

    @ApiOperation(value = "/zlog/zOperateLog/update", notes = "修改-操作日志表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('zlog:zOperateLog:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ZOperateLogDTO zOperateLogDTO) throws BaseException {
        try {
            ZOperateLog zOperateLog = zOperateLogConvert.dtoToEntity(zOperateLogDTO);
            zOperateLogService.updateById(zOperateLog);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("修改失败！请重试");
        }
    }

    @ApiOperation(value = "/zlog/zOperateLog/delete", notes = "删除-操作日志表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logIds", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('zlog:zOperateLog:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] logIds) throws BaseException {
        try {
            zOperateLogService.removeBatchByIds(Arrays.asList(logIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("删除失败！请重试");
        }
    }

    @ApiOperation(value = "/zlog/zOperateLog/export/excel", notes = "导出excel-操作日志表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('zlog:zOperateLog:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zOperateLogService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }
}
