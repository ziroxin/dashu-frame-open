package com.kg.module.generator.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.module.generator.dto.ZFormGeneratorDTO;
import com.kg.module.generator.dto.convert.ZFormGeneratorConvert;
import com.kg.module.generator.entity.ZFormGenerator;
import com.kg.module.generator.excels.ZFormGeneratorExcelConstant;
import com.kg.module.generator.excels.ZFormGeneratorExcelOutDTO;
import com.kg.module.generator.mapper.ZFormGeneratorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 代码生成器表单 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-11-22
 */
@Service
public class ZFormGeneratorServiceImpl extends ServiceImpl<ZFormGeneratorMapper, ZFormGenerator> implements ZFormGeneratorService {

    @Resource
    private ZFormGeneratorConvert zFormGeneratorConvert;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZFormGeneratorDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZFormGenerator> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZFormGenerator> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("formId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("formId")), ZFormGenerator::getFormId, paramObj.getStr("formId"));
            }
            if (paramObj.containsKey("formName")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("formName")), ZFormGenerator::getFormName, paramObj.getStr("formName"));
            }
            if (paramObj.containsKey("formJson")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("formJson")), ZFormGenerator::getFormJson, paramObj.getStr("formJson"));
            }
            if (paramObj.containsKey("tableName")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("tableName")), ZFormGenerator::getTableName, paramObj.getStr("tableName"));
            }
            if (paramObj.containsKey("tableDecription")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("tableDecription")), ZFormGenerator::getTableDecription, paramObj.getStr("tableDecription"));
            }
            if (paramObj.containsKey("basePackage")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("basePackage")), ZFormGenerator::getBasePackage, paramObj.getStr("basePackage"));
            }
            if (paramObj.containsKey("author")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("author")), ZFormGenerator::getAuthor, paramObj.getStr("author"));
            }
            if (paramObj.containsKey("tablePackage")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tablePackage")), ZFormGenerator::getTablePackage, paramObj.getStr("tablePackage"));
            }
            if (paramObj.containsKey("viewPath")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("viewPath")), ZFormGenerator::getViewPath, paramObj.getStr("viewPath"));
            }
            if (paramObj.containsKey("status")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("status")), ZFormGenerator::getStatus, paramObj.getStr("status"));
            }
            if (paramObj.containsKey("orderIndex")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ZFormGenerator::getOrderIndex, paramObj.getStr("orderIndex"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZFormGenerator::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZFormGenerator::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        // 默认排序
        wrapper.lambda().orderByAsc(ZFormGenerator::getOrderIndex);
        //返回数据
        Page<ZFormGenerator> pageEntity = page(pager, wrapper);
        Page<ZFormGeneratorDTO> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> zFormGeneratorConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zFormGeneratorDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String add(ZFormGeneratorDTO zFormGeneratorDTO) {
        ZFormGenerator zFormGenerator = zFormGeneratorConvert.dtoToEntity(zFormGeneratorDTO);
        zFormGenerator.setFormId(GuidUtils.getUuid());
        zFormGenerator.setCreateTime(LocalDateTime.now());
        zFormGenerator.setStatus("0");
        save(zFormGenerator);
        return zFormGenerator.getFormId();
    }

    /**
     * 修改
     *
     * @param zFormGeneratorDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZFormGeneratorDTO zFormGeneratorDTO) {
        ZFormGenerator zFormGenerator = zFormGeneratorConvert.dtoToEntity(zFormGeneratorDTO);
        zFormGenerator.setUpdateTime(LocalDateTime.now());
        updateById(zFormGenerator);
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
            QueryWrapper<ZFormGenerator> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("formId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("formId")), ZFormGenerator::getFormId, paramObj.getStr("formId"));
                }
                if (paramObj.containsKey("formName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("formName")), ZFormGenerator::getFormName, paramObj.getStr("formName"));
                }
                if (paramObj.containsKey("formJson")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("formJson")), ZFormGenerator::getFormJson, paramObj.getStr("formJson"));
                }
                if (paramObj.containsKey("tableName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tableName")), ZFormGenerator::getTableName, paramObj.getStr("tableName"));
                }
                if (paramObj.containsKey("tableDecription")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tableDecription")), ZFormGenerator::getTableDecription, paramObj.getStr("tableDecription"));
                }
                if (paramObj.containsKey("basePackage")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("basePackage")), ZFormGenerator::getBasePackage, paramObj.getStr("basePackage"));
                }
                if (paramObj.containsKey("author")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("author")), ZFormGenerator::getAuthor, paramObj.getStr("author"));
                }
                if (paramObj.containsKey("tablePackage")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("tablePackage")), ZFormGenerator::getTablePackage, paramObj.getStr("tablePackage"));
                }
                if (paramObj.containsKey("viewPath")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("viewPath")), ZFormGenerator::getViewPath, paramObj.getStr("viewPath"));
                }
                if (paramObj.containsKey("orderIndex")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ZFormGenerator::getOrderIndex, paramObj.getStr("orderIndex"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZFormGenerator::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZFormGenerator::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZFormGenerator> list = list(wrapper);
            // 转换成导出excel实体
            List<ZFormGeneratorExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZFormGeneratorExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZFormGeneratorExcelOutDTO());
            }
            // 第一行标题
            String title = "代码生成器表单";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZFormGeneratorExcelConstant.EXPORT_EXCEL_COLUMN);
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
    public void importExcel(HttpServletRequest request) {
        // 读取导入数据
        List<ZFormGenerator> importData =
                ExcelReadUtils.read(request, 1, 2, ZFormGenerator.class, ZFormGeneratorExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZFormGenerator> saveData = importData.stream().map(o -> {
            o.setFormId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
