package ${package.ServiceImpl};

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import ${superServiceImplClassPackage};
import com.kg.component.file.FileNameUtils;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import ${package.Entity}.${entity};
import ${package.ExcelConstant}.${entity}ExcelConstant;
import ${package.ExcelOut}.${entity}ExcelOutDTO;
import ${package.Mapper}.${table.mapperName};
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
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
            String path = FileNameUtils.SAVE_PATH + "/exportTemp/excel/"
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<${entity}> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
<#list table.fields as field>
                if (paramObj.containsKey("${field.propertyName}")) {
                    wrapper.lambda().eq(${entity}::get${field.propertyName?cap_first}, paramObj.getStr("${field.propertyName}"));
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
            return FileNameUtils.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
</#if>
