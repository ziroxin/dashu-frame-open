package ${package.Service};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${superServiceClassPackage};
import ${package.DTO}.${dtoName};
import ${package.Entity}.${entity};

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @param sorts  排序条件
     * @return 分页列表
     */
    Page<${dtoName}> pagelist(Integer page, Integer limit, String params, String sorts);

    /**
     * 新增
     *
     * @param ${dtoName?uncap_first} 新增实体
     */
    void add(${dtoName} ${dtoName?uncap_first});

    /**
     * 修改
     *
     * @param ${dtoName?uncap_first} 编辑实体
     */
    void update(${dtoName} ${dtoName?uncap_first});

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    void delete(List<String> idlist);

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @param sorts  排序条件
     * @return 导出后的文件url
     */
    String exportExcel(String params, String sorts);

    /**
     * 导入Excel
     *
     * @param request 请求文件
     */
    String importExcel(HttpServletRequest request);

    /**
     * 下载导入模板
     *
     * @return 模板文件url
     */
    String downloadTemplate();
}
</#if>
