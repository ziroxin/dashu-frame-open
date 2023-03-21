package ${package.ServiceImpl};

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${superServiceImplClassPackage};
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import ${package.Entity}.${entity};
import ${package.DTO}.${dtoName};
import ${package.Convert}.${dtoconvertName};
import ${package.ExcelConstant}.${entity}ExcelConstant;
import ${package.ExcelOut}.${entity}ExcelOutDTO;
import ${package.Mapper}.${table.mapperName};
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Resource
    private ${dtoconvertName} ${dtoconvertName?uncap_first};


    /**
     *  分页列表
     *
     * @param page 页码
     * @param limit 条数
     * @param params 查询条件
     * @return
     */
    @Override
    public Page<${dtoName}> pagelist(Integer page, Integer limit, String params) {
        Page<${entity}> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<${entity}> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
        JSONObject paramObj = JSONUtil.parseObj(params);
        <#list table.fields as field>
            if (paramObj.containsKey("${field.propertyName}")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("${field.propertyName}")), ${entity}::get${field.propertyName?cap_first}, paramObj.getStr("${field.propertyName}"));
            }
        </#list>
        }
        <#list table.fields as field>
            <#if field.propertyName=="orderIndex">
        // 默认排序
        wrapper.lambda().orderByAsc(${entity}::getOrderIndex);
            </#if>
        </#list>
        //返回数据
        Page<${entity}> pageEntity = page(pager, wrapper);
        Page<${dtoName}> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> ${dtoconvertName?uncap_first}.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }



   /**
    * 新增
    *
    * @param ${dtoName?uncap_first} 新增实体
    */
    @Override
    public void add(${dtoName} ${dtoName?uncap_first}) {
        ${entity} ${entity?uncap_first} = ${dtoconvertName?uncap_first}.dtoToEntity(${dtoName?uncap_first});
        <#if idType == "ASSIGN_UUID">
        ${entity?uncap_first}.set${entityKeyName?cap_first}(GuidUtils.getUuid());
        </#if>
        <#list table.fields as field>
            <#if field.propertyName=="createTime">
        ${entity?uncap_first}.setCreateTime(LocalDateTime.now());
            </#if>
        </#list>
        save(${entity?uncap_first});
    }

    /**
    * 修改
    *
    * @param ${dtoName?uncap_first} 编辑实体
    */
    @Override
    public void update(${dtoName} ${dtoName?uncap_first}) {
        ${entity} ${entity?uncap_first} = ${dtoconvertName?uncap_first}.dtoToEntity(${dtoName?uncap_first});
        <#list table.fields as field>
            <#if field.propertyName=="updateTime">
        ${entity?uncap_first}.setUpdateTime(LocalDateTime.now());
            </#if>
        </#list>
        updateById(${entity?uncap_first});
    }


    /**
    * 删除
    *
    * @param idlist 删除id列表
    */
    @Override
    public void delete(List<String> idlist) {
        removeBatchByIds(idlist);
    }

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    @Override
    public String exportExcel(String params) {
        try {
            // 拼接导出Excel的文件，保存的临时路径
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/"
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<${entity}> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
<#list table.fields as field>
                if (paramObj.containsKey("${field.propertyName}")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("${field.propertyName}")), ${entity}::get${field.propertyName?cap_first}, paramObj.getStr("${field.propertyName}"));
                }
</#list>
            }
            List<${entity}> list = list(wrapper);
            // 转换成导出excel实体
            List<${entity}ExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ${entity}ExcelOutDTO.class))
                    .collect(Collectors.toList());
            // 第一行标题
            String title = "${table.comment!}";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, ${entity}ExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }



}
</#if>
