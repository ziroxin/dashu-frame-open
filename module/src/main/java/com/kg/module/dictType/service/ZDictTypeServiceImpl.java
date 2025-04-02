package com.kg.module.dictType.service;

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
import com.kg.core.exception.BaseException;
import com.kg.module.dictData.entity.ZDictData;
import com.kg.module.dictData.service.ZDictDataService;
import com.kg.module.dictType.dto.ZDictTypeDTO;
import com.kg.module.dictType.dto.convert.ZDictTypeConvert;
import com.kg.module.dictType.entity.ZDictType;
import com.kg.module.dictType.excels.ZDictTypeExcelConstant;
import com.kg.module.dictType.excels.ZDictTypeExcelOutDTO;
import com.kg.module.dictType.mapper.ZDictTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典类型 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-05-26
 */
@Service
public class ZDictTypeServiceImpl extends ServiceImpl<ZDictTypeMapper, ZDictType> implements ZDictTypeService {

    @Resource
    private ZDictTypeConvert zDictTypeConvert;
    @Resource
    private ZDictDataService zDictDataService;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZDictTypeDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZDictType> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZDictType> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("typeId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("typeId")), ZDictType::getTypeId, paramObj.getStr("typeId"));
            }
            if (paramObj.containsKey("typeName")) {
                wrapper.lambda()
                        .like(StringUtils.hasText(paramObj.getStr("typeName")), ZDictType::getTypeName, paramObj.getStr("typeName"))
                        .or()
                        .like(StringUtils.hasText(paramObj.getStr("typeName")), ZDictType::getTypeCode, paramObj.getStr("typeName"));
            }
            if (paramObj.containsKey("typeCode")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("typeCode")), ZDictType::getTypeCode, paramObj.getStr("typeCode"));
            }
            if (paramObj.containsKey("status")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("status")), ZDictType::getStatus, paramObj.getStr("status"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZDictType::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZDictType::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        //返回数据
        Page<ZDictType> pageEntity = page(pager, wrapper);
        Page<ZDictTypeDTO> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> zDictTypeConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zDictTypeDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZDictTypeDTO zDictTypeDTO) throws BaseException {
        // 查重
        if (lambdaQuery().eq(ZDictType::getTypeCode, zDictTypeDTO.getTypeCode()).count() > 0) {
            throw new BaseException("字典Code 已存在，请修改！");
        }
        ZDictType zDictType = zDictTypeConvert.dtoToEntity(zDictTypeDTO);
        zDictType.setTypeId(GuidUtils.getUuid());
        zDictType.setCreateTime(LocalDateTime.now());
        save(zDictType);
    }

    /**
     * 修改
     *
     * @param zDictTypeDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZDictTypeDTO zDictTypeDTO) throws BaseException {
        // 查重
        if (lambdaQuery().eq(ZDictType::getTypeCode, zDictTypeDTO.getTypeCode())
                .ne(ZDictType::getTypeId, zDictTypeDTO.getTypeId()).count() > 0) {
            throw new BaseException("字典Code 已存在，请修改！");
        }
        ZDictType zDictType = zDictTypeConvert.dtoToEntity(zDictTypeDTO);
        zDictType.setUpdateTime(LocalDateTime.now());
        updateById(zDictType);
    }

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(List<String> idlist) throws BaseException {
        // 查询是否有数据，有数据不能删除
        List<String> typeCodeList = new ArrayList<>();
        List<ZDictType> dictTypeList = lambdaQuery().in(ZDictType::getTypeId, idlist).list();
        dictTypeList.stream().forEach(m -> {
            List<ZDictData> dataList = zDictDataService.lambdaQuery().eq(ZDictData::getTypeCode, m.getTypeCode()).list();
            if (dataList != null && dataList.size() > 0) {
                typeCodeList.add(m.getTypeCode());
            }
        });
        if (typeCodeList != null && typeCodeList.size() > 0) {
            throw new BaseException("字典 [ " + typeCodeList.stream().collect(Collectors.joining(",")) + " ] 下有数据，不能删除");
        }
        // 无数据，删除
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
            QueryWrapper<ZDictType> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("typeId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("typeId")), ZDictType::getTypeId, paramObj.getStr("typeId"));
                }
                if (paramObj.containsKey("typeName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("typeName")), ZDictType::getTypeName, paramObj.getStr("typeName"));
                }
                if (paramObj.containsKey("typeCode")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("typeCode")), ZDictType::getTypeCode, paramObj.getStr("typeCode"));
                }
                if (paramObj.containsKey("status")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("status")), ZDictType::getStatus, paramObj.getStr("status"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZDictType::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZDictType::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZDictType> list = list(wrapper);
            // 转换成导出excel实体
            List<ZDictTypeExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZDictTypeExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZDictTypeExcelOutDTO());
            }
            // 第一行标题
            String title = "字典类型";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZDictTypeExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ZDictType> importData =
                ExcelReadUtils.read(request, 1, 2, ZDictType.class, ZDictTypeExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZDictType> saveData = importData.stream().map(o -> {
            o.setTypeId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
