package ${package.Controller};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import ${package.DTO}.${dtoName};
import ${package.Convert}.${dtoconvertName};
import ${package.Service}.${service};
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
<#if !restControllerStyle>
	import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Resource
    private ${table.serviceName} ${table.serviceName?uncap_first};
    @Resource
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
    public Page<${dtoName}> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                               @RequestParam(value = "params", required = false) String params) {
        return ${table.serviceName?uncap_first}.pagelist(page, limit, params);
    }

    @ApiOperation(value = "${controllerMapping}/add", notes = "新增-${table.comment!}", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('${controllerAuthorizePre}add')")
    @NoRepeatSubmit
    public void add(@RequestBody ${dtoName} ${dtoName?uncap_first}) throws BaseException {
        try {
            ${table.serviceName?uncap_first}.add(${dtoName?uncap_first});
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "新增失败！请重试");
        }
    }

    @ApiOperation(value = "${controllerMapping}/update", notes = "修改-${table.comment!}", httpMethod = "PUT")
    @ApiImplicitParams({})
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('${controllerAuthorizePre}update')")
    @NoRepeatSubmit
    public void update(@RequestBody ${dtoName} ${dtoName?uncap_first}) throws BaseException {
        try {
            ${table.serviceName?uncap_first}.update(${dtoName?uncap_first});
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "修改失败！请重试");
        }
    }

    @ApiOperation(value = "${controllerMapping}/delete", notes = "删除-${table.comment!}", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "${entityKeyName}s", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('${controllerAuthorizePre}delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] ${entityKeyName}s) throws BaseException {
        try {
            ${table.serviceName?uncap_first}.delete(Arrays.asList(${entityKeyName}s));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }

    @ApiOperation(value = "${controllerMapping}/export/excel", notes = "导出excel-${table.comment!}", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('${controllerAuthorizePre}export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = ${table.serviceName?uncap_first}.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }

    @ApiOperation(value = "${controllerMapping}/import/excel", notes = "导入excel-${table.comment!}", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "HttpServletRequest")
    })
    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('${controllerAuthorizePre}import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        try {
            ${table.serviceName?uncap_first}.importExcel(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("导入Excel失败，请重试！");
        }
    }
}
</#if>
