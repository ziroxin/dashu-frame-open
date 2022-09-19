package ${package.Controller};

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.utils.GuidUtils;
import ${package.Entity}.${entity};
import ${package.Service}.${service};
import ${package.Convert}.${dtoconvertName};
import ${package.DTO}.${dtoName};
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
<#if !restControllerStyle>
	import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 * ${table.comment!} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("${controllerMapping}")
@Api(tags = "${table.controllerName}", value = "${table.comment!}", description = "${table.comment!}")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    @Autowired
    private ${table.serviceName} ${table.serviceName?uncap_first};
	@Autowired
	private ${dtoconvertName} ${dtoconvertName?uncap_first};

	@ApiOperation(value = "${controllerMapping}/getById", notes = "详情-${table.comment!}", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "${entityKeyName}", value = "ID", paramType = "query", required = true, dataType = "String")
	})
	@GetMapping("/getById")
	@PreAuthorize("hasAuthority('${controllerAuthorizePre}getById')")
	public ${dtoName} getById(String ${entityKeyName}) {
		return ${dtoconvertName?uncap_first}.entityToDto(${table.serviceName?uncap_first}.getById(${entityKeyName}));
	}

	@ApiOperation(value = "${controllerMapping}/list", notes = "分页列表-${table.comment!}", httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
	})
	@GetMapping("/list")
	@PreAuthorize("hasAuthority('${controllerAuthorizePre}list')")
	public Page<${entity}> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
							@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
							@RequestParam(value = "params", required = false) String params) {
		Page<${entity}> pager = new Page<>(page, limit);
		// 根据条件查询
		QueryWrapper<${entity}> wrapper = new QueryWrapper<>();
		if (StringUtils.hasText(params)) {
			JSONObject paramObj = JSONUtil.parseObj(params);
<#list table.fields as field>
			if (paramObj.containsKey("${field.propertyName}")) {
				wrapper.lambda().eq(${entity}::get${field.propertyName?cap_first}, paramObj.getStr("${field.propertyName}"));
			}
	<#if field.propertyName=="order_index">
			// 默认排序
			wrapper.lambda().orderByAsc(${entity}::getOrderIndex);
	</#if>
</#list>
		}
		return ${table.serviceName?uncap_first}.page(pager, wrapper);
	}

    @ApiOperation(value = "${controllerMapping}/add", notes = "新增-${table.comment!}", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('${controllerAuthorizePre}add')")
    public boolean add(@RequestBody ${dtoName} ${dtoName?uncap_first}) {
    	${entity} ${entity?uncap_first} = ${dtoconvertName?uncap_first}.dtoToEntity(${dtoName?uncap_first});
        ${entity?uncap_first}.set${entityKeyName?cap_first}(GuidUtils.getUuid());
<#list table.fields as field>
	<#if field.propertyName=="create_time">
		${entity?uncap_first}.setCreateTime(LocalDateTime.now());
	</#if>
</#list>
        if (${table.serviceName?uncap_first}.save(${entity?uncap_first})) {
        	return true;
        }
		return false;
	}

	@ApiOperation(value = "${controllerMapping}/update", notes = "修改-${table.comment!}", httpMethod = "POST")
	@ApiImplicitParams({})
	@PostMapping("/update")
	@PreAuthorize("hasAuthority('${controllerAuthorizePre}update')")
	public boolean update(@RequestBody ${dtoName} ${dtoName?uncap_first}) {
    	${entity} ${entity?uncap_first} = ${dtoconvertName?uncap_first}.dtoToEntity(${dtoName?uncap_first});
<#list table.fields as field>
	<#if field.propertyName=="update_time">
		${entity?uncap_first}.setUpdateTime(LocalDateTime.now());
	</#if>
</#list>
		if (${table.serviceName?uncap_first}.updateById(${entity?uncap_first})) {
			return true;
		}
		return false;
	}

	@ApiOperation(value = "${controllerMapping}/delete", notes = "删除-${table.comment!}", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "${entityKeyName}s", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
	})
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('${controllerAuthorizePre}delete')")
	public boolean delete(@RequestBody String[] ${entityKeyName}s) {
		if (${table.serviceName?uncap_first}.removeBatchByIds(Arrays.asList(${entityKeyName}s))) {
			return true;
		}
		return false;
	}

	// todo 导出Excel
}
</#if>
