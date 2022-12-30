package com.kg.core.zsafety.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.exception.BaseException;
import com.kg.core.zsafety.dto.ZSafetyDTO;
import com.kg.core.zsafety.dto.convert.ZSafetyConvert;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
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
 * 密码安全等设置 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2022-12-30
 */
@RestController
@RequestMapping("/zsafety/zSafety")
@Api(tags = "ZSafetyController", value = "密码安全等设置", description = "密码安全等设置")
public class ZSafetyController {

    @Resource
    private ZSafetyService zSafetyService;
	@Resource
	private ZSafetyConvert zSafetyConvert;

	@ApiOperation(value = "/zsafety/zSafety/getById", notes = "详情-密码安全等设置", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "ID", paramType = "query", required = true, dataType = "String")
	})
	@GetMapping("/getById")
	@PreAuthorize("hasAuthority('zsafety:zSafety:getById')")
	public ZSafetyDTO getById(String id) {
		return zSafetyConvert.entityToDto(zSafetyService.getById(id));
	}

	@ApiOperation(value = "/zsafety/zSafety/list", notes = "分页列表-密码安全等设置", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
	})
	@GetMapping("/list")
	@PreAuthorize("hasAuthority('zsafety:zSafety:list')")
	public Page<ZSafety> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
							@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
							@RequestParam(value = "params", required = false) String params) {
		Page<ZSafety> pager = new Page<>(page, limit);
		// 根据条件查询
		QueryWrapper<ZSafety> wrapper = new QueryWrapper<>();
		if (StringUtils.hasText(params)) {
			JSONObject paramObj = JSONUtil.parseObj(params);
			if (paramObj.containsKey("id")) {
				wrapper.lambda().eq(ZSafety::getId, paramObj.getStr("id"));
			}
			if (paramObj.containsKey("startLength")) {
				wrapper.lambda().eq(ZSafety::getStartLength, paramObj.getStr("startLength"));
			}
			if (paramObj.containsKey("endLength")) {
				wrapper.lambda().eq(ZSafety::getEndLength, paramObj.getStr("endLength"));
			}
			if (paramObj.containsKey("lowercase")) {
				wrapper.lambda().eq(ZSafety::getLowercase, paramObj.getStr("lowercase"));
			}
			if (paramObj.containsKey("uppercase")) {
				wrapper.lambda().eq(ZSafety::getUppercase, paramObj.getStr("uppercase"));
			}
			if (paramObj.containsKey("numbers")) {
				wrapper.lambda().eq(ZSafety::getNumbers, paramObj.getStr("numbers"));
			}
			if (paramObj.containsKey("specialCharacters")) {
				wrapper.lambda().eq(ZSafety::getSpecialCharacters, paramObj.getStr("specialCharacters"));
			}
			if (paramObj.containsKey("banUsername")) {
				wrapper.lambda().eq(ZSafety::getBanUsername, paramObj.getStr("banUsername"));
			}
			if (paramObj.containsKey("validTime")) {
				wrapper.lambda().eq(ZSafety::getValidTime, paramObj.getStr("validTime"));
			}
			if (paramObj.containsKey("prompt")) {
				wrapper.lambda().eq(ZSafety::getPrompt, paramObj.getStr("prompt"));
			}
			if (paramObj.containsKey("loginFailedTimes")) {
				wrapper.lambda().eq(ZSafety::getLoginFailedTimes, paramObj.getStr("loginFailedTimes"));
			}
			if (paramObj.containsKey("lockTime")) {
				wrapper.lambda().eq(ZSafety::getLockTime, paramObj.getStr("lockTime"));
			}
			if (paramObj.containsKey("defaultPassword")) {
				wrapper.lambda().eq(ZSafety::getDefaultPassword, paramObj.getStr("defaultPassword"));
			}
			if (paramObj.containsKey("updateTime")) {
				wrapper.lambda().eq(ZSafety::getUpdateTime, paramObj.getStr("updateTime"));
			}
		}

		return zSafetyService.page(pager, wrapper);
	}

    @ApiOperation(value = "/zsafety/zSafety/add", notes = "新增-密码安全等设置", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('zsafety:zSafety:add')")
    public void add(@RequestBody ZSafetyDTO zSafetyDTO) throws BaseException {
        try {
            ZSafety zSafety = zSafetyConvert.dtoToEntity(zSafetyDTO);
            zSafetyService.save(zSafety);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("新增失败！请重试");
        }
	}

	@ApiOperation(value = "/zsafety/zSafety/update", notes = "修改-密码安全等设置", httpMethod = "POST")
	@ApiImplicitParams({})
	@PostMapping("/update")
	@PreAuthorize("hasAuthority('zsafety:zSafety:update')")
	public void update(@RequestBody ZSafetyDTO zSafetyDTO) throws BaseException {
		try {
			ZSafety zSafety = zSafetyConvert.dtoToEntity(zSafetyDTO);
			zSafety.setUpdateTime(LocalDateTime.now());
			zSafetyService.updateById(zSafety);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException("修改失败！请重试");
		}
	}

	@ApiOperation(value = "/zsafety/zSafety/delete", notes = "删除-密码安全等设置", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
	})
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('zsafety:zSafety:delete')")
	public void delete(@RequestBody String[] ids) throws BaseException {
		try {
			zSafetyService.removeBatchByIds(Arrays.asList(ids));
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException("删除失败！请重试");
		}
	}

	@ApiOperation(value = "/zsafety/zSafety/export/excel", notes = "导出excel-密码安全等设置", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
	})
	@GetMapping("/export/excel")
	@PreAuthorize("hasAuthority('zsafety:zSafety:export:excel')")
	public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
		String result = zSafetyService.exportExcel(params);
		if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
		}
		return result;
	}
}
