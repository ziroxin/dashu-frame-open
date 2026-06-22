package ${package.Mapper};

import cn.hutool.json.JSONObject;
import ${superMapperClassPackage};
import ${package.DTO}.${dtoName};
import ${package.Entity}.${entity};
<#if mapperAnnotationClass??>
import ${mapperAnnotationClass.name};
</#if>
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Repository
<#if mapperAnnotationClass??>
@${mapperAnnotationClass.simpleName}
</#if>
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {

    /**
     * 根据参数查询列表
     *
     * @param paramObj 参数对象
     * @return 列表
     */
    List<${dtoName}> list(JSONObject paramObj);

    /**
     * 根据参数查询总数
     *
     * @param paramObj 参数对象
     * @return 总数
     */
    long count(JSONObject paramObj);

    /**
     * 批量保存
     *
     * @param saveData 批量保存数据
     */
    void saveList(List<${entity}> saveData);

}
</#if>
