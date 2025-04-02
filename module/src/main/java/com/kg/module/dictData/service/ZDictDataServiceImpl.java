package com.kg.module.dictData.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.StrTypeCheckUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.module.dictData.dto.ZDictDataDTO;
import com.kg.module.dictData.dto.convert.ZDictDataConvert;
import com.kg.module.dictData.entity.ZDictData;
import com.kg.module.dictData.excels.ZDictDataExcelConstant;
import com.kg.module.dictData.excels.ZDictDataExcelImportDTO;
import com.kg.module.dictData.excels.ZDictDataExcelOutDTO;
import com.kg.module.dictData.mapper.ZDictDataMapper;
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
 * 字典数据 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@Service
public class ZDictDataServiceImpl extends ServiceImpl<ZDictDataMapper, ZDictData> implements ZDictDataService {

    @Resource
    private ZDictDataConvert zDictDataConvert;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZDictDataDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZDictData> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZDictData> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("dictId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("dictId")), ZDictData::getDictId, paramObj.getStr("dictId"));
            }
            if (paramObj.containsKey("typeCode")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("typeCode")), ZDictData::getTypeCode, paramObj.getStr("typeCode"));
            }
            if (paramObj.containsKey("dictLabel")) {
                wrapper.lambda().and(wr -> {
                    wr.like(StringUtils.hasText(paramObj.getStr("dictLabel")), ZDictData::getDictLabel, paramObj.getStr("dictLabel"))
                            .or().like(StringUtils.hasText(paramObj.getStr("dictLabel")), ZDictData::getDictValue, paramObj.getStr("dictLabel"));
                });
            }
            if (paramObj.containsKey("dictValue")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("dictValue")), ZDictData::getDictValue, paramObj.getStr("dictValue"));
            }
            if (paramObj.containsKey("status")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("status")), ZDictData::getStatus, paramObj.getStr("status"));
            }
            if (paramObj.containsKey("orderIndex")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ZDictData::getOrderIndex, paramObj.getStr("orderIndex"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZDictData::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZDictData::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        // 默认排序
        wrapper.lambda().orderByAsc(ZDictData::getOrderIndex);
        //返回数据
        Page<ZDictData> pageEntity = page(pager, wrapper);
        Page<ZDictDataDTO> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> zDictDataConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zDictDataDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZDictDataDTO zDictDataDTO) {
        // 查重
        List<ZDictData> dataList = lambdaQuery().eq(ZDictData::getTypeCode, zDictDataDTO.getTypeCode())
                .and(query -> query.eq(ZDictData::getDictValue, zDictDataDTO.getDictValue())
                        .or().eq(ZDictData::getDictLabel, zDictDataDTO.getDictLabel()))
                .list();
        if (dataList != null && dataList.size() > 0) {
            throw new RuntimeException("字典数据已存在，请修改！");
        }
        ZDictData zDictData = zDictDataConvert.dtoToEntity(zDictDataDTO);
        zDictData.setDictId(GuidUtils.getUuid());
        zDictData.setCreateTime(LocalDateTime.now());
        save(zDictData);
    }

    /**
     * 修改
     *
     * @param zDictDataDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZDictDataDTO zDictDataDTO) {
        // 查重
        List<ZDictData> dataList = lambdaQuery().eq(ZDictData::getTypeCode, zDictDataDTO.getTypeCode())
                .ne(ZDictData::getDictId, zDictDataDTO.getDictId()).and(wr -> {
                    wr.eq(ZDictData::getDictValue, zDictDataDTO.getDictValue())
                            .or().eq(ZDictData::getDictLabel, zDictDataDTO.getDictLabel());
                }).list();
        if (dataList != null && dataList.size() > 0) {
            throw new RuntimeException("字典数据已存在，请修改！");
        }
        ZDictData zDictData = zDictDataConvert.dtoToEntity(zDictDataDTO);
        zDictData.setUpdateTime(LocalDateTime.now());
        updateById(zDictData);
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
            QueryWrapper<ZDictData> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("dictId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("dictId")), ZDictData::getDictId, paramObj.getStr("dictId"));
                }
                if (paramObj.containsKey("typeCode")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("typeCode")), ZDictData::getTypeCode, paramObj.getStr("typeCode"));
                }
                if (paramObj.containsKey("dictLabel")) {
                    wrapper.lambda().and(wr -> {
                        wr.eq(StringUtils.hasText(paramObj.getStr("dictLabel")), ZDictData::getDictLabel, paramObj.getStr("dictLabel"))
                                .or().eq(StringUtils.hasText(paramObj.getStr("dictLabel")), ZDictData::getDictValue, paramObj.getStr("dictLabel"));
                    });
                }
                if (paramObj.containsKey("dictValue")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("dictValue")), ZDictData::getDictValue, paramObj.getStr("dictValue"));
                }
                if (paramObj.containsKey("status")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("status")), ZDictData::getStatus, paramObj.getStr("status"));
                }
                if (paramObj.containsKey("orderIndex")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ZDictData::getOrderIndex, paramObj.getStr("orderIndex"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZDictData::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZDictData::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            wrapper.lambda().orderByAsc(ZDictData::getOrderIndex);
            List<ZDictData> list = list(wrapper);
            // 转换成导出excel实体
            List<ZDictDataExcelOutDTO> dataList = list.stream()
                    .map(d -> {
                        ZDictDataExcelOutDTO b = JSONUtil.toBean(JSONUtil.parseObj(d, true), ZDictDataExcelOutDTO.class);
                        b.setStatus("0".equals(d.getStatus()) ? "停用" : "正常");
                        return b;
                    })
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZDictDataExcelOutDTO());
            }
            // 第一行标题
            String title = "数据字典";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZDictDataExcelConstant.EXPORT_EXCEL_COLUMN);
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
        String typeCode = request.getParameter("typeCode");
        // 读取导入数据
        int startRowIdx = 2;
        List<ZDictDataExcelImportDTO> importData =
                ExcelReadUtils.read(request, 1, startRowIdx, ZDictDataExcelImportDTO.class, ZDictDataExcelConstant.IMPORT_EXCEL_COLUMN);
        if (importData == null || importData.isEmpty()) {
            return "Excel文件中没有数据！";
        }
        // 2. 必填字段校验
        String errorMsg = "";
        int currentRowIdx = startRowIdx;
        if (ZDictDataExcelConstant.IMPORT_REQUIRED_COLUMN.size() > 0) {
            for (ZDictDataExcelImportDTO entity : importData) {
                currentRowIdx++;
                JSONObject rowData = JSONUtil.parseObj(entity);
                List<String> emptyColName = new ArrayList<>();
                for (Map.Entry<String, String> col : ZDictDataExcelConstant.IMPORT_REQUIRED_COLUMN.entrySet()) {
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

        // 3. Excel表内查重
        Map<String, Long> labelCountMap = importData.stream().map(ZDictDataExcelImportDTO::getDictLabel)
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        Map<String, Long> valueCountMap = importData.stream().map(ZDictDataExcelImportDTO::getDictValue)
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        currentRowIdx = startRowIdx;
        for (ZDictDataExcelImportDTO entity : importData) {
            currentRowIdx++;
            if (labelCountMap.get(entity.getDictLabel()) != null && labelCountMap.get(entity.getDictLabel()) > 1) {
                errorMsg += "第" + currentRowIdx + "行，数据标签[" + entity.getDictLabel() + "]在Excel表内重复！<br/>";
            }
            if (valueCountMap.get(entity.getDictValue()) != null && valueCountMap.get(entity.getDictValue()) > 1) {
                errorMsg += "第" + currentRowIdx + "行，数据值[" + entity.getDictValue() + "]在Excel表内重复！<br/>";
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }

        // 4. 数据库查重
        List<ZDictData> list = lambdaQuery().eq(ZDictData::getTypeCode, typeCode).list();
        currentRowIdx = startRowIdx;
        for (ZDictDataExcelImportDTO entity : importData) {
            currentRowIdx++;
            if (list.stream().anyMatch(d -> d.getDictValue().equals(entity.getDictLabel()))) {
                errorMsg += "第" + currentRowIdx + "行，数据标签已存在！<br/>";
            }
            if (list.stream().anyMatch(d -> d.getDictLabel().equals(entity.getDictValue()))) {
                errorMsg += "第" + currentRowIdx + "行，数据值已存在！<br/>";
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }

        // 5. 保存数据
        List<ZDictData> saveData = importData.stream().map(o -> {
            ZDictData entity = JSONUtil.toBean(JSONUtil.parseObj(JSONUtil.toJsonStr(o)), ZDictData.class);
            entity.setDictId(GuidUtils.getUuid());
            entity.setTypeCode(typeCode);
            entity.setStatus("1");// 默认正常（0：停用，1：正常）
            entity.setCreateTime(LocalDateTime.now());
            return entity;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
        return "";
    }


    @Override
    public List<ZDictData> listCache(String typeCode) {
        String key = CacheConstant.DICT_TYPE_REDIS_PRE + typeCode;
        if (redisUtils.hasKey(key)) {
            return JSONUtil.toList(redisUtils.get(key).toString(), ZDictData.class);
        }
        // 查询所有字典数据
        List<ZDictData> dataList = lambdaQuery().eq(ZDictData::getTypeCode, typeCode)
                .orderByAsc(ZDictData::getOrderIndex).list();
        if (dataList != null && dataList.size() > 0) {
            // 将缓存存入redis
            redisUtils.set(key, JSONUtil.toJsonStr(dataList));
            return dataList;
        }
        return new ArrayList<>();
    }

    /**
     * 清空数据字典缓存数据
     */
    @Override
    public void clearCache(String typeCode) {
        if (StringUtils.hasText(typeCode)) {
            // 清空一个缓存
            redisUtils.delete(CacheConstant.DICT_TYPE_REDIS_PRE + typeCode);
        } else {
            // 清空全部缓存
            List<ZDictData> dataList = lambdaQuery().list();
            if (dataList != null && dataList.size() > 0) {
                dataList.forEach(o -> {
                    redisUtils.delete(CacheConstant.DICT_TYPE_REDIS_PRE + o.getTypeCode());
                });
            }
        }
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
            String title = "数据字典-导入模板";
            // 写入模板字段行
            ExcelWriteUtils.writeTemplate(path, title, ZDictDataExcelConstant.IMPORT_EXCEL_COLUMN);
            // 生成模板成功，返回模板地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

}
