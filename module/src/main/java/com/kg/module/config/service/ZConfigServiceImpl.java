package com.kg.module.config.service;

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
import com.kg.module.config.dto.ZConfigDTO;
import com.kg.module.config.dto.convert.ZConfigConvert;
import com.kg.module.config.entity.ZConfig;
import com.kg.module.config.excels.ZConfigExcelConstant;
import com.kg.module.config.excels.ZConfigExcelOutDTO;
import com.kg.module.config.mapper.ZConfigMapper;
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
 * 参数参数配置 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2024-05-27
 */
@Service
public class ZConfigServiceImpl extends ServiceImpl<ZConfigMapper, ZConfig> implements ZConfigService {

    @Resource
    private ZConfigConvert zConfigConvert;


    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZConfigDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZConfig> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZConfig> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("cfgId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgId")), ZConfig::getCfgId, paramObj.getStr("cfgId"));
            }
            if (paramObj.containsKey("cfgName")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("cfgName")), ZConfig::getCfgName, paramObj.getStr("cfgName"));
            }
            if (paramObj.containsKey("cfgKey")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("cfgKey")), ZConfig::getCfgKey, paramObj.getStr("cfgKey"));
            }
            if (paramObj.containsKey("cfgValue")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgValue")), ZConfig::getCfgValue, paramObj.getStr("cfgValue"));
            }
            if (paramObj.containsKey("cfgRemark")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgRemark")), ZConfig::getCfgRemark, paramObj.getStr("cfgRemark"));
            }
            if (paramObj.containsKey("orderIndex")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ZConfig::getOrderIndex, paramObj.getStr("orderIndex"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZConfig::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZConfig::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        // 默认排序
        wrapper.lambda().orderByAsc(ZConfig::getOrderIndex);
        //返回数据
        Page<ZConfig> pageEntity = page(pager, wrapper);
        if (pageEntity.getTotal() == 0) {
            return new Page<>();
        }
        Page<ZConfigDTO> result = new Page<>();
        result.setRecords(pageEntity.getRecords().stream()
                .map(m -> {
                    ZConfigDTO zConfigDTO = zConfigConvert.entityToDto(m);
                    return zConfigDTO;
                })
                .collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zConfigDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZConfigDTO zConfigDTO) {
        ZConfig zConfig = zConfigConvert.dtoToEntity(zConfigDTO);
        zConfig.setCfgId(GuidUtils.getUuid());
        zConfig.setCreateTime(LocalDateTime.now());
        save(zConfig);
    }

    /**
     * 修改
     *
     * @param zConfigDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZConfigDTO zConfigDTO) {
        ZConfig zConfig = zConfigConvert.dtoToEntity(zConfigDTO);
        zConfig.setUpdateTime(LocalDateTime.now());
        updateById(zConfig);
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
            QueryWrapper<ZConfig> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("cfgId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgId")), ZConfig::getCfgId, paramObj.getStr("cfgId"));
                }
                if (paramObj.containsKey("cfgName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgName")), ZConfig::getCfgName, paramObj.getStr("cfgName"));
                }
                if (paramObj.containsKey("cfgKey")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgKey")), ZConfig::getCfgKey, paramObj.getStr("cfgKey"));
                }
                if (paramObj.containsKey("cfgValue")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgValue")), ZConfig::getCfgValue, paramObj.getStr("cfgValue"));
                }
                if (paramObj.containsKey("cfgRemark")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("cfgRemark")), ZConfig::getCfgRemark, paramObj.getStr("cfgRemark"));
                }
                if (paramObj.containsKey("orderIndex")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ZConfig::getOrderIndex, paramObj.getStr("orderIndex"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZConfig::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZConfig::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZConfig> list = list(wrapper);
            // 转换成导出excel实体
            List<ZConfigExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZConfigExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZConfigExcelOutDTO());
            }
            // 第一行标题
            String title = "参数参数配置";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZConfigExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ZConfig> importData =
                ExcelReadUtils.read(request, 1, 2, ZConfig.class, ZConfigExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZConfig> saveData = importData.stream().map(o -> {
            o.setCfgId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
