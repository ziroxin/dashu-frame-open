package com.kg.module.atest.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.atest.dto.ATestDTO;
import com.kg.module.atest.dto.convert.ATestConvert;
import com.kg.module.atest.service.ATestService;
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
 * 测试表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-03-30
 */
@RestController
@RequestMapping("/atest/aTest")
@Api(tags = "ATestController", value = "测试表", description = "测试表")
public class ATestController {

    @Resource
    private ATestService aTestService;
    @Resource
    private ATestConvert aTestConvert;

    @ApiOperation(value = "/atest/aTest/getById", notes = "详情-测试表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('atest:aTest:getById')")
    public ATestDTO getById(String testId) {
        return aTestConvert.entityToDto(aTestService.getById(testId));
    }

    @ApiOperation(value = "/atest/aTest/list", notes = "分页列表-测试表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('atest:aTest:list')")
    public Page<ATestDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return aTestService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/atest/aTest/add", notes = "新增-测试表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('atest:aTest:add')")
    @NoRepeatSubmit
    public void add(@RequestBody ATestDTO aTestDTO) throws BaseException {
        try {
            aTestService.add(aTestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/atest/aTest/update", notes = "修改-测试表", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('atest:aTest:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ATestDTO aTestDTO) throws BaseException {
        try {
            aTestService.update(aTestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/atest/aTest/delete", notes = "删除-测试表", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "testIds", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('atest:aTest:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] testIds) throws BaseException {
        try {
            aTestService.delete(Arrays.asList(testIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/atest/aTest/export/excel", notes = "导出excel-测试表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('atest:aTest:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = aTestService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/atest/aTest/import/excel", notes = "导入excel-测试表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('atest:aTest:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            aTestService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
