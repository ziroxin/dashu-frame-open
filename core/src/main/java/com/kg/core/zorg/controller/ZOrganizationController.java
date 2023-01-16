package com.kg.core.zorg.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.utils.GuidUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.zorg.dto.ZOrganizationTreeSelectDTO;
import com.kg.core.zorg.dto.ZOrganizationDTO;
import com.kg.core.zorg.dto.convert.ZOrganizationConvert;
import com.kg.core.zorg.entity.ZOrganization;
import com.kg.core.zorg.service.ZOrganizationService;
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
import java.util.List;
import java.util.Optional;

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

    @ApiOperation(value = "/zorg/zOrganization/tree", notes = "组织机构树", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName", value = "名称模糊查询", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "父级ID", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('zorg:zOrganization:tree')")
    public List<ZOrganizationDTO> tree(@RequestParam(value = "orgName", required = false) String orgName,
                                       @RequestParam(value = "parentId", required = false) String parentId) {
        return zOrganizationService.tree(orgName, parentId);
    }

    @ApiOperation(value = "/zorg/zOrganization/treeForSelect", notes = "下拉选择框组织机构树", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父级ID", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/treeForSelect")
    @PreAuthorize("hasAuthority('zorg:zOrganization:treeForSelect')")
    public List<ZOrganizationTreeSelectDTO> treeForSelect(@RequestParam(value = "parentId", required = false) String parentId) {
        return zOrganizationService.treeForSelect(parentId);
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
    public void add(@RequestBody ZOrganizationDTO zOrganizationDTO) throws BaseException {
        try {
            ZOrganization zOrganization = zOrganizationConvert.dtoToEntity(zOrganizationDTO);
            zOrganization.setOrgId(GuidUtils.getUuid32());
            if (StringUtils.hasText(zOrganization.getOrgParentId()) && !"-1".equals(zOrganization.getOrgParentId())) {
                // 有父级，取父级路径
                Optional<ZOrganization> oneOpt = zOrganizationService.lambdaQuery().eq(ZOrganization::getOrgId, zOrganization.getOrgParentId()).oneOpt();
                if (oneOpt.isPresent()) {
                    ZOrganization parent = oneOpt.get();
                    String parentPath = StringUtils.hasText(parent.getOrgPath()) ? parent.getOrgPath() + "." : "";
                    zOrganization.setOrgPath(parentPath + zOrganization.getOrgId());
                } else {
                    throw new BaseException("新增失败！您选择的父级不正确，请重试");
                }
            } else {
                zOrganization.setOrgParentId("-1");
                zOrganization.setOrgPath(zOrganization.getOrgId());
            }
            int pathLevel = StringUtils.hasText(zOrganization.getOrgPath()) ? zOrganization.getOrgPath().split("\\.").length : 0;
            zOrganization.setOrgLevel(pathLevel);
            zOrganization.setCreateTime(LocalDateTime.now());
            zOrganizationService.save(zOrganization);
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("新增失败！请重试");
        }
    }

    @ApiOperation(value = "/zorg/zOrganization/update", notes = "修改-组织机构表", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('zorg:zOrganization:update')")
    @NoRepeatSubmit
    public void update(@RequestBody ZOrganizationDTO zOrganizationDTO) throws BaseException {
        try {
            ZOrganization zOrganization = zOrganizationConvert.dtoToEntity(zOrganizationDTO);
            if (StringUtils.hasText(zOrganization.getOrgParentId()) && !"-1".equals(zOrganization.getOrgParentId())) {
                // 有父级，取父级路径
                Optional<ZOrganization> oneOpt = zOrganizationService.lambdaQuery().eq(ZOrganization::getOrgId, zOrganization.getOrgParentId()).oneOpt();
                if (oneOpt.isPresent()) {
                    ZOrganization parent = oneOpt.get();
                    String parentPath = StringUtils.hasText(parent.getOrgPath()) ? parent.getOrgPath() + "." : "";
                    zOrganization.setOrgPath(parentPath + zOrganization.getOrgId());
                } else {
                    throw new BaseException("新增失败！您选择的父级不正确，请重试");
                }
            } else {
                zOrganization.setOrgParentId("-1");
                zOrganization.setOrgPath(zOrganization.getOrgId());
            }
            int pathLevel = StringUtils.hasText(zOrganization.getOrgPath()) ? zOrganization.getOrgPath().split("\\.").length : 0;
            zOrganization.setOrgLevel(pathLevel);
            zOrganization.setUpdateTime(LocalDateTime.now());
            zOrganizationService.updateById(zOrganization);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("修改失败！请重试");
        }
    }

    @ApiOperation(value = "/zorg/zOrganization/delete", notes = "删除-组织机构表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgIds", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('zorg:zOrganization:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] orgIds) throws BaseException {
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
}
