package com.kg.core.zorg.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.web.ResponseResult;
import com.kg.core.zorg.dto.ZOrganizationDTO;
import com.kg.core.zorg.dto.convert.ZOrganizationConvert;
import com.kg.core.zorg.entity.ZOrganization;
import com.kg.core.zorg.service.ZOrganizationService;
import com.kg.core.zuser.dto.ZUserAllDTO;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 组织机构表 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-01-11
 */
@RestController
@RequestMapping("/zorg/zOrganization")
@Api(tags = "ZOrganizationController", value = "组织机构表", description = "组织机构表")
public class ZOrganizationController {

    @Resource
    private ZOrganizationService zOrganizationService;
    @Resource
    private ZOrganizationConvert zOrganizationConvert;
    @Resource
    private IZUserService userService;

    @ApiOperation(value = "/zorg/zOrganization/getMaxLevel", notes = "获取最大层级", httpMethod = "GET")
    @ApiImplicitParams({})
    @GetMapping("/getMaxLevel")
    public JSONObject getMaxLevel() {
        JSONObject result = new JSONObject();
        result.set("maxLevel", zOrganizationService.getMaxLevel());
        result.set("currentOrgId", CurrentUserUtils.getCurrentUserAll().getOrgId());
        return result;
    }

    @ApiOperation(value = "/zorg/zOrganization/tree", notes = "组织机构树", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName", value = "名称模糊查询", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('zorg:zOrganization:tree')")
    public List<ZOrganizationDTO> tree(@RequestParam(value = "orgName", required = false) String orgName) {
        ZUserAllDTO user = CurrentUserUtils.getCurrentUserAll();
        if (user == null || !StringUtils.hasText(user.getOrgId())) {
            return null;
        }
        if (user.getOrgId().equals("-1") || user.getOrgLevel() <= 1) {
            // 总管理员（当前用户的orgId=-1或者当前用户的orgLevel<=1代表总管理员），获取全部组织机构
            return zOrganizationService.tree(orgName, null);
        } else {
            // 非总管理员，获取当前用户所在组织及其下级组织
            return zOrganizationService.tree(orgName, user.getOrgId());
        }
    }

    @ApiOperation(value = "/zorg/zOrganization/parentTree", notes = "查询父级组织机构树", httpMethod = "GET")
    @ApiImplicitParams({})
    @GetMapping("/parentTree")
    @PreAuthorize("hasAuthority('zorg:zOrganization:parentTree')")
    public List<ZOrganizationDTO> parentTree() {
        return zOrganizationService.parentTree();
    }

    @ApiOperation(value = "/zorg/zOrganization/getById", notes = "详情-组织机构表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('zorg:zOrganization:getById')")
    public ZOrganizationDTO getById(String orgId) {
        return zOrganizationConvert.entityToDto(zOrganizationService.getById(orgId));
    }

    @ApiOperation(value = "/zorg/zOrganization/list", notes = "分页列表-组织机构表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('zorg:zOrganization:list')")
    public Page<ZOrganization> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                    @RequestParam(value = "params", required = false) String params) {
        Page<ZOrganization> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZOrganization> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("orgId")) {
                wrapper.lambda().eq(ZOrganization::getOrgId, paramObj.getStr("orgId"));
            }
            if (paramObj.containsKey("orgName")) {
                wrapper.lambda().like(ZOrganization::getOrgName, paramObj.getStr("orgName"));
            }
            if (paramObj.containsKey("orgParentId")) {
                wrapper.lambda().eq(ZOrganization::getOrgParentId, paramObj.getStr("orgParentId"));
            }
            if (paramObj.containsKey("orgPath")) {
                wrapper.lambda().eq(ZOrganization::getOrgPath, paramObj.getStr("orgPath"));
            }
            if (paramObj.containsKey("orgLevel")) {
                wrapper.lambda().eq(ZOrganization::getOrgLevel, paramObj.getStr("orgLevel"));
            }
            if (paramObj.containsKey("orderIndex")) {
                wrapper.lambda().eq(ZOrganization::getOrderIndex, paramObj.getStr("orderIndex"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(ZOrganization::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(ZOrganization::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }

        // 默认排序
        wrapper.lambda().orderByAsc(ZOrganization::getOrderIndex);
        return zOrganizationService.page(pager, wrapper);
    }

    @ApiOperation(value = "/zorg/zOrganization/add", notes = "新增-组织机构表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('zorg:zOrganization:add')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/zorg/zOrganization/add", logMsg = "新增-组织机构")
    public void add(@RequestBody ZOrganizationDTO zOrganizationDTO) throws BaseException {
        try {
            // 查重
            if (zOrganizationService.lambdaQuery()
                    .eq(ZOrganization::getOrgName, zOrganizationDTO.getOrgName()).exists()) {
                throw new BaseException("新增失败！组织机构名称重复，请重试");
            }
            zOrganizationService.add(zOrganizationDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "/zorg/zOrganization/update", notes = "修改-组织机构表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('zorg:zOrganization:update')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/zorg/zOrganization/update", logMsg = "修改-组织机构")
    public void update(@RequestBody ZOrganizationDTO zOrganizationDTO) throws BaseException {
        try {
            // 查重
            if (zOrganizationService.lambdaQuery().eq(ZOrganization::getOrgName, zOrganizationDTO.getOrgName())
                    .ne(ZOrganization::getOrgId, zOrganizationDTO.getOrgId()).exists()) {
                throw new BaseException("修改失败！组织机构名称重复，请重试");
            }
            zOrganizationService.update(zOrganizationDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "/zorg/zOrganization/delete", notes = "删除-组织机构表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('zorg:zOrganization:delete')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/zorg/zOrganization/delete", logMsg = "删除-组织机构")
    public void delete(@RequestBody String[] orgIds) throws BaseException {
        // 是否满足删除条件：1 没有子级 2 没有归属用户
        if (zOrganizationService.lambdaQuery().in(ZOrganization::getOrgParentId, orgIds).count() > 0) {
            throw new BaseException("删除失败！您要删除的组织机构，存在子节点，请先删除子节点");
        }
        if (userService.lambdaQuery().in(ZUser::getOrgId, orgIds).count() > 0) {
            throw new BaseException("删除失败！您要删除的组织机构，已被用户使用，请先删除关联关系");
        }
        try {
            zOrganizationService.removeBatchByIds(Arrays.asList(orgIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("删除失败！请重试");
        }
    }

    @ApiOperation(value = "/zorg/zOrganization/export/excel", notes = "导出excel-组织机构表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('zorg:zOrganization:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = zOrganizationService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "/zorg/zOrganization/import/excel", notes = "导入excel-组织机构表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('zorg:zOrganization:import:excel')")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/zorg/zOrganization/import/excel", logMsg = "导入组织机构")
    public ResponseResult importExcel(HttpServletRequest request) throws BaseException {
        try {
            String result = zOrganizationService.importExcel(request);
            if (StringUtils.hasText(result)) {
                // 导入失败，返回错误提示信息
                return ResponseResult.builder().code("200").message(result).build();
            } else {
                return ResponseResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "导入Excel失败，请重试！");
        }
    }

    @ApiOperation(value = "/zorg/zOrganization/import/downloadTemplate", notes = "下载导入模板-组织机构表", httpMethod = "GET")
    @GetMapping("/import/downloadTemplate")
    public String downloadTemplate() throws BaseException {
        String result = zOrganizationService.downloadTemplate();
        if ("error".equals(result)) {
            throw new BaseException("下载导入模板失败，请重试！");
        }
        return result;
    }
}
