package ${package.ServiceImpl};

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${superServiceImplClassPackage};
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.StrTypeCheckUtils;
<#if (table.fields?exists) && (table.fields?size > 0)>
    <#assign hasCreateOrUpdateUserId = false>
    <#list table.fields as field>
        <#if field.propertyName == "createUserId" || field.propertyName == "updateUserId">
            <#assign hasCreateOrUpdateUserId = true>
            <#break>
        </#if>
    </#list>
    <#if hasCreateOrUpdateUserId>
import com.kg.core.security.util.CurrentUserUtils;
    </#if>
</#if>
<#if childTableList??>
    <#list childTableList as child>
import ${packageBaseParent}.${child}.entity.${child?cap_first};
import ${packageBaseParent}.${child}.service.${child?cap_first}Service;
    </#list>
</#if>
import ${package.DTO}.${dtoName};
import ${package.Convert}.${dtoconvertName};
import ${package.Entity}.${entity};
import ${package.ExcelConstant}.${entity}ExcelConstant;
import ${package.ExcelImport}.${entity}ExcelImportDTO;
import ${package.ExcelOut}.${entity}ExcelOutDTO;
import ${package.Mapper}.${table.mapperName};
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    @Resource
    private ${table.mapperName} ${table.mapperName?uncap_first};

<#if childTableList??>
    <#list childTableList as child>
    @Resource
    private ${child?cap_first}Service ${child}Service;
    </#list>
</#if>

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @param sorts  排序条件
     * @return 分页列表
     */
    @Override
    public Page<${dtoName}> pagelist(Integer page, Integer limit, String params, String sorts) {
        // 解析查询参数
        JSONObject paramObj = new JSONObject();
        if (StringUtils.hasText(params)) {
            paramObj = JSONUtil.parseObj(params);
        }
        // 计算分页偏移量
        Integer offset = (page - 1) * limit;
        paramObj.set("limit", limit);
        paramObj.set("offset", offset);
        // 处理排序
        if (StringUtils.hasText(sorts)) {
            // 例如：{"columnStr": "字段名", "orderStr": "ASC/DESC"}
            JSONObject sortObj = JSONUtil.parseObj(sorts);
            if (sortObj.size() > 0) {
                paramObj.set(sortObj.getStr("column") + "Order", sortObj.getStr("order"));
            }
        }
        // 查询列表
        List<${dtoName}> list = ${table.mapperName?uncap_first}.list(paramObj);
<#if childTableList??>
        if (list != null && !list.isEmpty()) {
            // 查询所有附件列表
    <#list childTableList as child>
            List<${child?cap_first}> all${child?cap_first}List = ${child}Service.lambdaQuery().in(${child?cap_first}::get${entity}Id,
                    list.stream().map(${dtoName}::get${entityKeyName?cap_first}).collect(Collectors.toList())).list();
    </#list>
            // 过滤附件列表，放入实体中
            list.stream().forEach(dto -> {
    <#list childTableList as child>
                dto.set${child?lower_case?cap_first}List(all${child?cap_first}List.stream()
                        .filter(f -> f.get${entity}Id().equals(dto.get${entityKeyName?cap_first}())).collect(Collectors.toList()));
    </#list>
            });
        }
</#if>
        Page<${dtoName}> result = new Page<>();
        result.setRecords(list);
        result.setTotal(${table.mapperName?uncap_first}.count(paramObj));// 查询总数
        return result;
    }

    /**
     * 新增
     *
     * @param ${dtoName?uncap_first} 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(${dtoName} ${dtoName?uncap_first}) {
        ${entity} ${entity?uncap_first} = ${dtoconvertName?uncap_first}.dtoToEntity(${dtoName?uncap_first});
        <#if idType == "ASSIGN_UUID">
        ${entity?uncap_first}.set${entityKeyName?cap_first}(GuidUtils.getUuid());
        </#if>
        <#list table.fields as field>
            <#if field.propertyName=="createTime">
        ${entity?uncap_first}.setCreateTime(LocalDateTime.now());
            </#if>
            <#if field.propertyName=="createUserId">
        ${entity?uncap_first}.setCreateUserId(CurrentUserUtils.getCurrentUser().getUserId());
            </#if>
        </#list>
        save(${entity?uncap_first});
<#if childTableList??>
        // 保存附件
    <#list childTableList as child>
        if (${dtoName?uncap_first}.get${child?lower_case?cap_first}List() != null && ${dtoName?uncap_first}.get${child?lower_case?cap_first}List().size() > 0) {
            List<${child?cap_first}> save${child?cap_first}List = ${dtoName?uncap_first}.get${child?lower_case?cap_first}List()
                    .stream().map(m -> {
                        m.set${entity}Id(${entity?uncap_first}.get${entityKeyName?cap_first}());
                        m.setCreateTime(LocalDateTime.now());
                        return m;
                    }).collect(Collectors.toList());
            ${child}Service.saveBatch(save${child?cap_first}List);
        }
    </#list>
</#if>
    }

    /**
     * 修改
     *
     * @param ${dtoName?uncap_first} 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(${dtoName} ${dtoName?uncap_first}) {
        ${entity} ${entity?uncap_first} = ${dtoconvertName?uncap_first}.dtoToEntity(${dtoName?uncap_first});
        <#list table.fields as field>
            <#if field.propertyName=="updateTime">
        ${entity?uncap_first}.setUpdateTime(LocalDateTime.now());
            </#if>
            <#if field.propertyName=="updateUserId">
        ${entity?uncap_first}.setUpdateUserId(CurrentUserUtils.getCurrentUser().getUserId());
            </#if>
        </#list>
        updateById(${entity?uncap_first});
<#if childTableList??>
        // 先删除附件
    <#list childTableList as child>
        ${child}Service.lambdaUpdate().eq(${child?cap_first}::get${entity}Id, ${entity?uncap_first}.get${entityKeyName?cap_first}()).remove();
    </#list>
        // 再保存附件
    <#list childTableList as child>
        if (${dtoName?uncap_first}.get${child?lower_case?cap_first}List() != null && ${dtoName?uncap_first}.get${child?lower_case?cap_first}List().size() > 0) {
            List<${child?cap_first}> save${child?cap_first}List = ${dtoName?uncap_first}.get${child?lower_case?cap_first}List()
                    .stream().map(m -> {
                        m.set${entity}Id(${entity?uncap_first}.get${entityKeyName?cap_first}());
                        m.setCreateTime(LocalDateTime.now());
                        return m;
                    }).collect(Collectors.toList());
            ${child}Service.saveBatch(save${child?cap_first}List);
        }
    </#list>
</#if>
    }

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(List<String> idlist) {
        removeBatchByIds(idlist);
<#if childTableList??>
        // 删除附件
    <#list childTableList as child>
        ${child}Service.lambdaUpdate().in(${child?cap_first}::get${entity}Id, idlist).remove();
    </#list>
</#if>
    }

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @param sorts  排序条件
     * @return 导出后的文件url
     */
    @Override
    public String exportExcel(String params, String sorts) {
        try {
            // 拼接导出Excel的文件，保存的临时路径
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/"
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            JSONObject paramObj = new JSONObject();
            if (StringUtils.hasText(params)) {
                paramObj = JSONUtil.parseObj(params);
            }

            // 处理排序
            if (StringUtils.hasText(sorts)) {
                // 例如：{"columnStr": "字段名", "orderStr": "ASC/DESC"}
                JSONObject sortObj = JSONUtil.parseObj(sorts);
                if (sortObj.size() > 0) {
                    paramObj.set(sortObj.getStr("column") + "Order", sortObj.getStr("order"));
                }
            }

            // 查询列表
            List<${dtoName}> list = ${table.mapperName?uncap_first}.list(paramObj);
            // 转换成导出excel实体
            List<${entity}ExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ${entity}ExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ${entity}ExcelOutDTO());
            }
            // 第一行标题
            String title = "${table.comment!}";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ${entity}ExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 导入Excel
     *
     * @param request 请求文件
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String importExcel(HttpServletRequest request) {
        // 1. 读取导入数据
        int startRowIdx = 2;
        List<${entity}ExcelImportDTO> importData =
                ExcelReadUtils.read(request, 1, startRowIdx, ${entity}ExcelImportDTO.class, ${entity}ExcelConstant.IMPORT_EXCEL_COLUMN);
        if (importData == null || importData.isEmpty()) {
            return "Excel文件中没有数据！";
        }
        // 2. 必填字段校验
        String errorMsg = "";
        int currentRowIdx = startRowIdx;
        if (${entity}ExcelConstant.IMPORT_REQUIRED_COLUMN.size() > 0) {
            for (${entity}ExcelImportDTO entity : importData) {
                currentRowIdx++;
                JSONObject rowData = JSONUtil.parseObj(entity);
                List<String> emptyColName = new ArrayList<>();
                for (Map.Entry<String, String> col : ${entity}ExcelConstant.IMPORT_REQUIRED_COLUMN.entrySet()) {
                    if (!StringUtils.hasText(rowData.getStr(col.getKey()))) {
                        emptyColName.add(col.getValue());
                    }
                }
                if (emptyColName.size() > 0) {
                    errorMsg += "第" + currentRowIdx + "行，必填字段[" + String.join(",", emptyColName) + "]不能为空！<br/>";
                }
<#list table.fields as field>
    <#if field.propertyName=='orderIndex'>
                // 检测数字格式
                if (StringUtils.hasText(entity.getOrderIndex()) && !StrTypeCheckUtils.isNumeric(entity.getOrderIndex())) {
                    errorMsg += "第" + currentRowIdx + "行，${field.comment}必须是数字！<br/>";
                }
    </#if>
</#list>
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }
        // 3. 保存
        List<${entity}> saveData = importData.stream().map(obj -> {
            ${entity} o = JSONUtil.toBean(JSONUtil.parseObj(obj), ${entity}.class);
            o.set${entityKeyName?cap_first}(GuidUtils.getUuid());
<#list table.fields as field>
    <#if field.propertyName=="createTime">
            o.setCreateTime(LocalDateTime.now());
    </#if>
    <#if field.propertyName=="createUserId">
            o.setCreateUserId(CurrentUserUtils.getCurrentUser().getUserId());
    </#if>
</#list>
            return o;
        }).collect(Collectors.toList());
        ${table.mapperName?uncap_first}.saveList(saveData);
        return "";// 导入成功，返回空字符串
    }

    /**
     * 下载导入模板
     *
     * @return 模板文件url
     */
    @Override
    public String downloadTemplate() {
        try {
            // 拼接下载Excel模板，保存的临时路径
            String path = FilePathConfig.SAVE_PATH + "/importTemp/excel/"
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";
            // 第一行标题
            String title = "${table.comment!}-导入模板";
            // 写入模板字段行
            ExcelWriteUtils.writeTemplate(path, title, ${entity}ExcelConstant.IMPORT_EXCEL_COLUMN);
            // 生成模板成功，返回模板地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

}
</#if>
