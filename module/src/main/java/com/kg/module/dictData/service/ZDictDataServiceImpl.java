package com.kg.module.dictData.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.module.dictData.dto.ZDictDataDTO;
import com.kg.module.dictData.dto.convert.ZDictDataConvert;
import com.kg.module.dictData.entity.ZDictData;
import com.kg.module.dictData.excels.ZDictDataExcelConstant;
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
            JSONObject paramObj = JSONUtil.parseObj(params);
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
        List<ZDictData> dataList = lambdaQuery().eq(ZDictData::getDictValue, zDictDataDTO.getDictValue())
                .or().eq(ZDictData::getDictLabel, zDictDataDTO.getDictLabel()).list();
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
        List<ZDictData> dataList = lambdaQuery()
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
                JSONObject paramObj = JSONUtil.parseObj(params);
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
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZDictDataExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZDictDataExcelOutDTO());
            }
            // 第一行标题
            String title = "字典数据表格";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, ZDictDataExcelConstant.EXPORT_EXCEL_COLUMN);
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
        String typeCode = request.getParameter("typeCode");
        // 读取导入数据
        List<ZDictData> importData =
                ExcelCommonUtils.read(request, 1, 2, ZDictData.class, ZDictDataExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZDictData> saveData = importData.stream().map(o -> {
            o.setDictId(GuidUtils.getUuid());
            o.setTypeCode(typeCode);
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }


    @Override
    public List<ZDictData> listCache(String typeCode) {
        String key = CacheConstant.DICT_TYPE_REDIS_PRE + typeCode;
        if (redisUtils.hasKey(key)) {
            return JSONUtil.toList(redisUtils.get(key).toString(), ZDictData.class);
        }
        // 查询所有字典数据
        List<ZDictData> dataList = lambdaQuery().eq(ZDictData::getTypeCode, typeCode).list();
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

}
