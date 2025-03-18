package com.kg.module.atable.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.StrTypeCheckUtils;
import com.kg.module.aTableFiles.entity.ATableFiles;
import com.kg.module.aTableFiles.service.ATableFilesService;
import com.kg.module.aTableImages.entity.ATableImages;
import com.kg.module.aTableImages.service.ATableImagesService;
import com.kg.module.atable.dto.ATableDTO;
import com.kg.module.atable.dto.convert.ATableConvert;
import com.kg.module.atable.entity.ATable;
import com.kg.module.atable.excels.ATableExcelConstant;
import com.kg.module.atable.excels.ATableExcelImportDTO;
import com.kg.module.atable.excels.ATableExcelOutDTO;
import com.kg.module.atable.mapper.ATableMapper;
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
 * 我的表a_table 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@Service
public class ATableServiceImpl extends ServiceImpl<ATableMapper, ATable> implements ATableService {

    @Resource
    private ATableConvert aTableConvert;
    @Resource
    private ATableMapper aTableMapper;

    @Resource
    private ATableFilesService aTableFilesService;
    @Resource
    private ATableImagesService aTableImagesService;

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
    public Page<ATableDTO> pagelist(Integer page, Integer limit, String params, String sorts) {
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
        List<ATableDTO> list = aTableMapper.list(paramObj);
        if (list != null && !list.isEmpty()) {
            // 查询所有附件列表
            List<ATableFiles> allATableFilesList = aTableFilesService.lambdaQuery().in(ATableFiles::getATableId,
                    list.stream().map(ATableDTO::getId).collect(Collectors.toList())).list();
            List<ATableImages> allATableImagesList = aTableImagesService.lambdaQuery().in(ATableImages::getATableId,
                    list.stream().map(ATableDTO::getId).collect(Collectors.toList())).list();
            // 过滤附件列表，放入实体中
            list.stream().forEach(dto -> {
                dto.setAtablefilesList(allATableFilesList.stream()
                        .filter(f -> f.getATableId().equals(dto.getId())).collect(Collectors.toList()));
                dto.setAtableimagesList(allATableImagesList.stream()
                        .filter(f -> f.getATableId().equals(dto.getId())).collect(Collectors.toList()));
            });
        }
        Page<ATableDTO> result = new Page<>();
        result.setRecords(list);
        result.setTotal(aTableMapper.count(paramObj));// 查询总数
        return result;
    }

    /**
     * 新增
     *
     * @param aTableDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ATableDTO aTableDTO) {
        ATable aTable = aTableConvert.dtoToEntity(aTableDTO);
        aTable.setId(GuidUtils.getUuid());
        aTable.setCreateTime(LocalDateTime.now());
        save(aTable);
        // 保存附件
        if (aTableDTO.getAtablefilesList() != null && aTableDTO.getAtablefilesList().size() > 0) {
            List<ATableFiles> saveATableFilesList = aTableDTO.getAtablefilesList()
                    .stream().map(m -> {
                        m.setATableId(aTable.getId());
                        m.setCreateTime(LocalDateTime.now());
                        return m;
                    }).collect(Collectors.toList());
            aTableFilesService.saveBatch(saveATableFilesList);
        }
        if (aTableDTO.getAtableimagesList() != null && aTableDTO.getAtableimagesList().size() > 0) {
            List<ATableImages> saveATableImagesList = aTableDTO.getAtableimagesList()
                    .stream().map(m -> {
                        m.setATableId(aTable.getId());
                        m.setCreateTime(LocalDateTime.now());
                        return m;
                    }).collect(Collectors.toList());
            aTableImagesService.saveBatch(saveATableImagesList);
        }
    }

    /**
     * 修改
     *
     * @param aTableDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ATableDTO aTableDTO) {
        ATable aTable = aTableConvert.dtoToEntity(aTableDTO);
        aTable.setUpdateTime(LocalDateTime.now());
        updateById(aTable);
        // 先删除附件
        aTableFilesService.lambdaUpdate().eq(ATableFiles::getATableId, aTable.getId()).remove();
        aTableImagesService.lambdaUpdate().eq(ATableImages::getATableId, aTable.getId()).remove();
        // 再保存附件
        if (aTableDTO.getAtablefilesList() != null && aTableDTO.getAtablefilesList().size() > 0) {
            List<ATableFiles> saveATableFilesList = aTableDTO.getAtablefilesList()
                    .stream().map(m -> {
                        m.setATableId(aTable.getId());
                        m.setCreateTime(LocalDateTime.now());
                        return m;
                    }).collect(Collectors.toList());
            aTableFilesService.saveBatch(saveATableFilesList);
        }
        if (aTableDTO.getAtableimagesList() != null && aTableDTO.getAtableimagesList().size() > 0) {
            List<ATableImages> saveATableImagesList = aTableDTO.getAtableimagesList()
                    .stream().map(m -> {
                        m.setATableId(aTable.getId());
                        m.setCreateTime(LocalDateTime.now());
                        return m;
                    }).collect(Collectors.toList());
            aTableImagesService.saveBatch(saveATableImagesList);
        }
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
        // 删除附件
        aTableFilesService.lambdaUpdate().in(ATableFiles::getATableId, idlist).remove();
        aTableImagesService.lambdaUpdate().in(ATableImages::getATableId, idlist).remove();
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
            List<ATableDTO> list = aTableMapper.list(paramObj);
            // 转换成导出excel实体
            List<ATableExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ATableExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ATableExcelOutDTO());
            }
            // 第一行标题
            String title = "我的表a_table";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ATableExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ATableExcelImportDTO> importData =
                ExcelReadUtils.read(request, 1, startRowIdx, ATableExcelImportDTO.class, ATableExcelConstant.IMPORT_EXCEL_COLUMN);
        if (importData == null || importData.isEmpty()) {
            return "Excel文件中没有数据！";
        }
        // 2. 必填字段校验
        String errorMsg = "";
        int currentRowIdx = startRowIdx;
        if (ATableExcelConstant.IMPORT_REQUIRED_COLUMN.size() > 0) {
            for (ATableExcelImportDTO entity : importData) {
                currentRowIdx++;
                JSONObject rowData = JSONUtil.parseObj(entity);
                List<String> emptyColName = new ArrayList<>();
                for (Map.Entry<String, String> col : ATableExcelConstant.IMPORT_REQUIRED_COLUMN.entrySet()) {
                    if (!StringUtils.hasText(rowData.getStr(col.getKey()))) {
                        emptyColName.add(col.getValue());
                    }
                }
                if (emptyColName.size() > 0) {
                    errorMsg += "第" + currentRowIdx + "行，必填字段[" + String.join(",", emptyColName) + "]不能为空！<br/>";
                }
                // 检测数字格式
                if (StringUtils.hasText(entity.getOrderIndex()) && !StrTypeCheckUtils.isNumeric(entity.getOrderIndex())) {
                    errorMsg += "第" + currentRowIdx + "行，顺序必须是数字！<br/>";
                }
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }
        // 3. 保存
        List<ATable> saveData = importData.stream().map(obj -> {
            ATable o = JSONUtil.toBean(JSONUtil.parseObj(obj), ATable.class);
            o.setId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        aTableMapper.saveList(saveData);
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
            String title = "我的表a_table-导入模板";
            // 写入模板字段行
            ExcelWriteUtils.writeTemplate(path, title, ATableExcelConstant.IMPORT_EXCEL_COLUMN);
            // 生成模板成功，返回模板地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

}
