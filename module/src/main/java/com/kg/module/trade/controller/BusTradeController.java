package com.kg.module.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.trade.dto.BusTradeDTO;
import com.kg.module.trade.dto.convert.BusTradeConvert;
import com.kg.module.trade.service.BusTradeService;
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
 * 交易 - 支付demo 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-05-16
 */
@RestController
@RequestMapping("/trade/busTrade")
@Api(tags = "BusTradeController", value = "交易 - 支付demo", description = "交易 - 支付demo")
public class BusTradeController {

    @Resource
    private BusTradeService busTradeService;
    @Resource
    private BusTradeConvert busTradeConvert;

    @ApiOperation(value = "/trade/busTrade/getById", notes = "详情-交易 - 支付demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tradeId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('trade:busTrade:getById')")
    public BusTradeDTO getById(String tradeId) {
        return busTradeConvert.entityToDto(busTradeService.getById(tradeId));
    }

    @ApiOperation(value = "/trade/busTrade/list", notes = "分页列表-交易 - 支付demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('trade:busTrade:list')")
    public Page<BusTradeDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return busTradeService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/trade/busTrade/add", notes = "新增-交易 - 支付demo", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('trade:busTrade:add')")
    @NoRepeatSubmit
    public void add(@RequestBody BusTradeDTO busTradeDTO) throws BaseException {
        try {
            busTradeService.add(busTradeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/trade/busTrade/update", notes = "修改-交易 - 支付demo", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('trade:busTrade:update')")
    @NoRepeatSubmit
    public void update(@RequestBody BusTradeDTO busTradeDTO) throws BaseException {
        try {
            busTradeService.update(busTradeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/trade/busTrade/delete", notes = "删除-交易 - 支付demo", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tradeIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('trade:busTrade:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] tradeIds) throws BaseException {
        try {
            busTradeService.delete(Arrays.asList(tradeIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/trade/busTrade/export/excel", notes = "导出excel-交易 - 支付demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('trade:busTrade:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = busTradeService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/trade/busTrade/import/excel", notes = "导入excel-交易 - 支付demo", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('trade:busTrade:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            busTradeService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
