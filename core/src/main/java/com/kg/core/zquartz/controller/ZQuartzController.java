package com.kg.core.zquartz.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.utils.GuidUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.zquartz.config.QuartzConfig;
import com.kg.core.zquartz.dto.ZQuartzDTO;
import com.kg.core.zquartz.dto.convert.ZQuartzConvert;
import com.kg.core.zquartz.entity.ZQuartz;
import com.kg.core.zquartz.service.ZQuartzService;
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
 * 定时任务调度表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-12-26
 */
@RestController
@RequestMapping("/zquartz/zQuartz")
@Api(tags = "ZQuartzController", value = "定时任务调度表", description = "定时任务调度表")
public class ZQuartzController {

    @Resource
    private ZQuartzService zQuartzService;
    @Resource
    private ZQuartzConvert zQuartzConvert;
    @Resource
    private QuartzConfig quartzConfig;

    @ApiOperation(value = "/zquartz/zQuartz/refresh", notes = "刷新定时任务状态", httpMethod = "GET")
    @GetMapping("/refresh")
    @PreAuthorize("hasAuthority('zquartz:zQuartz:refresh')")
    public void refresh() throws BaseException {
        try {
            quartzConfig.refreshQuartzList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("刷新定时任务状态失败了，请重试！");
        }
    }

    @ApiOperation(value = "/zquartz/zQuartz/getById", notes = "详情-定时任务调度表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quartzId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('zquartz:zQuartz:getById')")
    public ZQuartzDTO getById(String quartzId) {
        return zQuartzConvert.entityToDto(zQuartzService.getById(quartzId));
    }

    @ApiOperation(value = "/zquartz/zQuartz/list", notes = "分页列表-定时任务调度表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('zquartz:zQuartz:list')")
    public Page<ZQuartz> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                              @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                              @RequestParam(value = "params", required = false) String params) {
        Page<ZQuartz> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZQuartz> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("quartzId")) {
                wrapper.lambda().eq(ZQuartz::getQuartzId, paramObj.getStr("quartzId"));
            }
            if (paramObj.containsKey("jobName")) {
                wrapper.lambda().like(ZQuartz::getJobName, paramObj.getStr("jobName"));
            }
            if (paramObj.containsKey("jobClass")) {
                wrapper.lambda().like(ZQuartz::getJobClass, paramObj.getStr("jobClass"));
            }
            if (paramObj.containsKey("jobTimeCron")) {
                wrapper.lambda().eq(ZQuartz::getJobTimeCron, paramObj.getStr("jobTimeCron"));
            }
            if (paramObj.containsKey("description")) {
                wrapper.lambda().eq(ZQuartz::getDescription, paramObj.getStr("description"));
            }
            if (paramObj.containsKey("status")) {
                wrapper.lambda().eq(ZQuartz::getStatus, paramObj.getStr("status"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(ZQuartz::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(ZQuartz::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }

        return zQuartzService.page(pager, wrapper);
    }

    @ApiOperation(value = "/zquartz/zQuartz/add", notes = "新增-定时任务调度表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('zquartz:zQuartz:add')")
    public void add(@RequestBody ZQuartzDTO zQuartzDTO) throws BaseException {
        try {
            ZQuartz zQuartz = zQuartzConvert.dtoToEntity(zQuartzDTO);
            zQuartz.setQuartzId(GuidUtils.getUuid());
            zQuartz.setCreateTime(LocalDateTime.now());
            zQuartzService.save(zQuartz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("新增失败！请重试");
        }
    }

    @ApiOperation(value = "/zquartz/zQuartz/update", notes = "修改-定时任务调度表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('zquartz:zQuartz:update')")
    public void update(@RequestBody ZQuartzDTO zQuartzDTO) throws BaseException {
        try {
            ZQuartz zQuartz = zQuartzConvert.dtoToEntity(zQuartzDTO);
            zQuartz.setUpdateTime(LocalDateTime.now());
            zQuartzService.updateById(zQuartz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("修改失败！请重试");
        }
    }

    @ApiOperation(value = "/zquartz/zQuartz/delete", notes = "删除-定时任务调度表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quartzIds", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('zquartz:zQuartz:delete')")
    public void delete(@RequestBody String[] quartzIds) throws BaseException {
        try {
            zQuartzService.removeBatchByIds(Arrays.asList(quartzIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("删除失败！请重试");
        }
    }

    @ApiOperation(value = "/zquartz/zQuartz/export/excel", notes = "导出excel-定时任务调度表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('zquartz:zQuartz:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zQuartzService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }
}
