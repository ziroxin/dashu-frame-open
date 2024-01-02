package com.kg.module.oauth2.client.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.module.oauth2.client.dto.OauthClientDetailsDTO;
import com.kg.module.oauth2.client.dto.convert.OauthClientDetailsConvert;
import com.kg.module.oauth2.client.service.OauthClientDetailsService;
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
 * Oauth2客户端信息表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-09-12
 */
@RestController
@RequestMapping("/oauth2.client/oauthClientDetails")
@Api(tags = "OauthClientDetailsController", value = "Oauth2客户端信息表", description = "Oauth2客户端信息表")
public class OauthClientDetailsController {

    @Resource
    private OauthClientDetailsService oauthClientDetailsService;
    @Resource
    private OauthClientDetailsConvert oauthClientDetailsConvert;

    @ApiOperation(value = "/oauth2.client/oauthClientDetails/getById", notes = "详情-Oauth2客户端信息表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('oauth2.client:oauthClientDetails:getById')")
    public OauthClientDetailsDTO getById(String clientId) {
        return oauthClientDetailsConvert.entityToDto(oauthClientDetailsService.getById(clientId));
    }

    @ApiOperation(value = "/oauth2.client/oauthClientDetails/list", notes = "分页列表-Oauth2客户端信息表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('oauth2.client:oauthClientDetails:list')")
    public Page<OauthClientDetailsDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                            @RequestParam(value = "params", required = false) String params) {
        return oauthClientDetailsService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/oauth2.client/oauthClientDetails/add", notes = "新增-Oauth2客户端信息表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('oauth2.client:oauthClientDetails:add')")
    @NoRepeatSubmit
    public void add(@RequestBody OauthClientDetailsDTO oauthClientDetailsDTO) throws BaseException {
        try {
            // 查重
            if (oauthClientDetailsService.getById(oauthClientDetailsDTO.getClientId()) != null) {
                throw new BaseException("应用ID已存在！请修改");
            }
            oauthClientDetailsService.add(oauthClientDetailsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/oauth2.client/oauthClientDetails/update", notes = "修改-Oauth2客户端信息表", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('oauth2.client:oauthClientDetails:update')")
    @NoRepeatSubmit
    public void update(@RequestBody OauthClientDetailsDTO oauthClientDetailsDTO) throws BaseException {
        try {
            oauthClientDetailsService.update(oauthClientDetailsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/oauth2.client/oauthClientDetails/delete", notes = "删除-Oauth2客户端信息表", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('oauth2.client:oauthClientDetails:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] clientIds) throws BaseException {
        try {
            oauthClientDetailsService.delete(Arrays.asList(clientIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/oauth2.client/oauthClientDetails/export/excel", notes = "导出excel-Oauth2客户端信息表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('oauth2.client:oauthClientDetails:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = oauthClientDetailsService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/oauth2.client/oauthClientDetails/import/excel", notes = "导入excel-Oauth2客户端信息表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('oauth2.client:oauthClientDetails:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            oauthClientDetailsService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
