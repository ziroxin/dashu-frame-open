package com.kg.module.tradeRefund.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.tradeRefund.dto.BusTradeRefundDTO;
import com.kg.module.tradeRefund.dto.convert.BusTradeRefundConvert;
import com.kg.module.tradeRefund.service.BusTradeRefundService;
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
 * 退款 - 支付demo 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-06-14
 */
@RestController
@RequestMapping("/tradeRefund/busTradeRefund")
@Api(tags = "BusTradeRefundController", value = "退款 - 支付demo", description = "退款 - 支付demo")
public class BusTradeRefundController {

    @Resource
    private BusTradeRefundService busTradeRefundService;
    @Resource
    private BusTradeRefundConvert busTradeRefundConvert;

    @ApiOperation(value = "/tradeRefund/busTradeRefund/getById", notes = "详情-退款 - 支付demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('tradeRefund:busTradeRefund:getById')")
    public BusTradeRefundDTO getById(String refundId) {
        return busTradeRefundConvert.entityToDto(busTradeRefundService.getById(refundId));
    }

    @ApiOperation(value = "/tradeRefund/busTradeRefund/list", notes = "分页列表-退款 - 支付demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('tradeRefund:busTradeRefund:list')")
    public Page<BusTradeRefundDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return busTradeRefundService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/tradeRefund/busTradeRefund/add", notes = "新增-退款 - 支付demo", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('tradeRefund:busTradeRefund:add')")
    @NoRepeatSubmit
    public void add(@RequestBody BusTradeRefundDTO busTradeRefundDTO) throws BaseException {
        try {
            busTradeRefundService.add(busTradeRefundDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/tradeRefund/busTradeRefund/update", notes = "修改-退款 - 支付demo", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('tradeRefund:busTradeRefund:update')")
    @NoRepeatSubmit
    public void update(@RequestBody BusTradeRefundDTO busTradeRefundDTO) throws BaseException {
        try {
            busTradeRefundService.update(busTradeRefundDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/tradeRefund/busTradeRefund/delete", notes = "删除-退款 - 支付demo", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('tradeRefund:busTradeRefund:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] refundIds) throws BaseException {
        try {
            busTradeRefundService.delete(Arrays.asList(refundIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/tradeRefund/busTradeRefund/export/excel", notes = "导出excel-退款 - 支付demo", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('tradeRefund:busTradeRefund:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = busTradeRefundService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/tradeRefund/busTradeRefund/import/excel", notes = "导入excel-退款 - 支付demo", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('tradeRefund:busTradeRefund:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            busTradeRefundService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
