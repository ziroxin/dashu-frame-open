package com.kg.module.userTheme.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.utils.GuidUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zuser.entity.ZUser;
import com.kg.module.userTheme.dto.ZUserThemeDTO;
import com.kg.module.userTheme.dto.convert.ZUserThemeConvert;
import com.kg.module.userTheme.entity.ZUserTheme;
import com.kg.module.userTheme.service.ZUserThemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * <p>
 * 用户主题配置 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-11-04
 */
@RestController
@RequestMapping("/userTheme/zUserTheme")
@Api(tags = "ZUserThemeController", value = "用户主题配置", description = "用户主题配置")
public class ZUserThemeController {

    @Resource
    private ZUserThemeService zUserThemeService;
    @Resource
    private ZUserThemeConvert zUserThemeConvert;

    @ApiOperation(value = "/userTheme/zUserTheme/getByUser", notes = "获取当前用户的主题配置", httpMethod = "GET")
    @GetMapping("/getByUser")
    public String getByUser() {
        try {
            ZUser currentUser = CurrentUserUtils.getCurrentUser();
            Optional<ZUserTheme> userTheme = zUserThemeService
                    .lambdaQuery().eq(ZUserTheme::getUserId, currentUser.getUserId()).oneOpt();
            if (userTheme.isPresent()) {
                return userTheme.get().getThemeJson();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //updateByUser
    @ApiOperation(value = "/userTheme/zUserTheme/updateByUser", notes = "新增-用户主题配置", httpMethod = "POST")
    @PostMapping("/updateByUser")
    @NoRepeatSubmit
    public void updateByUser(@RequestBody ZUserThemeDTO zUserThemeDTO) throws BaseException {
        try {
            ZUser currentUser = CurrentUserUtils.getCurrentUser();
            Optional<ZUserTheme> zUserTheme = zUserThemeService
                    .lambdaQuery().eq(ZUserTheme::getUserId, currentUser.getUserId()).oneOpt();
            if (zUserTheme.isPresent()) {
                // 存在，更新
                ZUserTheme theme = zUserTheme.get();
                JSONObject oldJson = JSONUtil.parseObj(theme.getThemeJson());
                JSONObject newJson = JSONUtil.parseObj(zUserThemeDTO.getThemeJson());
                // 遍历newJson所有key，更新oldJson
                for (String key : newJson.keySet()) {
                    oldJson.set(key, newJson.get(key));
                }
                theme.setThemeJson(oldJson.toString());
                theme.setUpdateTime(LocalDateTime.now());
                zUserThemeService.updateById(theme);
            } else {
                // 不存在，新增
                zUserThemeDTO.setThemeId(GuidUtils.getUuid());
                zUserThemeDTO.setUserId(currentUser.getUserId());
                zUserThemeDTO.setCreateTime(LocalDateTime.now());
                zUserThemeService.add(zUserThemeDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }


    @ApiOperation(value = "/userTheme/zUserTheme/getById", notes = "详情-用户主题配置", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "themeId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('userTheme:zUserTheme:getById')")
    public ZUserThemeDTO getById(String themeId) {
        return zUserThemeConvert.entityToDto(zUserThemeService.getById(themeId));
    }

    @ApiOperation(value = "/userTheme/zUserTheme/list", notes = "分页列表-用户主题配置", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('userTheme:zUserTheme:list')")
    public Page<ZUserThemeDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                    @RequestParam(value = "params", required = false) String params) {
        return zUserThemeService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/userTheme/zUserTheme/add", notes = "新增-用户主题配置", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('userTheme:zUserTheme:add')")
    @NoRepeatSubmit
    public void add(@RequestBody ZUserThemeDTO zUserThemeDTO) throws BaseException {
        try {
            zUserThemeService.add(zUserThemeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/userTheme/zUserTheme/update", notes = "修改-用户主题配置", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('userTheme:zUserTheme:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ZUserThemeDTO zUserThemeDTO) throws BaseException {
        try {
            zUserThemeService.update(zUserThemeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/userTheme/zUserTheme/delete", notes = "删除-用户主题配置", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "themeIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('userTheme:zUserTheme:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] themeIds) throws BaseException {
        try {
            zUserThemeService.delete(Arrays.asList(themeIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "/userTheme/zUserTheme/export/excel", notes = "导出excel-用户主题配置", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('userTheme:zUserTheme:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zUserThemeService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/userTheme/zUserTheme/import/excel", notes = "导入excel-用户主题配置", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('userTheme:zUserTheme:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            zUserThemeService.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
