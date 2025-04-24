package com.kg.core.ddos.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.TimeUtils;
import com.kg.core.ddos.dto.ZDdosDTO;
import com.kg.core.ddos.dto.convert.ZDdosConvert;
import com.kg.core.ddos.entity.ZDdos;
import com.kg.core.ddos.excels.ZDdosExcelConstant;
import com.kg.core.ddos.excels.ZDdosExcelOutDTO;
import com.kg.core.ddos.mapper.ZDdosMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * ddos用户请求记录 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2024-07-15
 */
@Service
public class ZDdosServiceImpl extends ServiceImpl<ZDdosMapper, ZDdos> implements ZDdosService {

    @Resource
    private ZDdosConvert zDdosConvert;


    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZDdosDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZDdos> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZDdos> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("ddosId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("ddosId")), ZDdos::getDdosId, paramObj.getStr("ddosId"));
            }
            if (paramObj.containsKey("userIp")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("userIp")), ZDdos::getUserIp, paramObj.getStr("userIp"));
            }
            if (paramObj.containsKey("requestCount")) {
                wrapper.lambda().gt(StringUtils.hasText(paramObj.getStr("requestCount")), ZDdos::getRequestCount, paramObj.getStr("requestCount"));
            }
            if (paramObj.containsKey("limitJson")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("limitJson")), ZDdos::getLimitJson, paramObj.getStr("limitJson"));
            }
            if (paramObj.containsKey("userId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("userId")), ZDdos::getUserId, paramObj.getStr("userId"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZDdos::getCreateTime, paramObj.getStr("createTime"));
            }
        }
        //返回数据
        Page<ZDdos> pageEntity = page(pager, wrapper);
        if (pageEntity.getTotal() == 0) {
            return new Page<>();
        }
        Page<ZDdosDTO> result = new Page<>();
        result.setRecords(pageEntity.getRecords().stream()
                .map(m -> {
                    ZDdosDTO zDdosDTO = zDdosConvert.entityToDto(m);
                    return zDdosDTO;
                })
                .collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zDdosDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZDdosDTO zDdosDTO) {
        ZDdos zDdos = zDdosConvert.dtoToEntity(zDdosDTO);
        zDdos.setDdosId(GuidUtils.getUuid());
        zDdos.setCreateTime(LocalDateTime.now());
        save(zDdos);
    }

    /**
     * 修改
     *
     * @param zDdosDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZDdosDTO zDdosDTO) {
        ZDdos zDdos = zDdosConvert.dtoToEntity(zDdosDTO);
        updateById(zDdos);
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
                    + TimeUtils.now().toFormat("yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<ZDdos> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("ddosId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("ddosId")), ZDdos::getDdosId, paramObj.getStr("ddosId"));
                }
                if (paramObj.containsKey("userIp")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("userIp")), ZDdos::getUserIp, paramObj.getStr("userIp"));
                }
                if (paramObj.containsKey("requestCount")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("requestCount")), ZDdos::getRequestCount, paramObj.getStr("requestCount"));
                }
                if (paramObj.containsKey("limitJson")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("limitJson")), ZDdos::getLimitJson, paramObj.getStr("limitJson"));
                }
                if (paramObj.containsKey("userId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("userId")), ZDdos::getUserId, paramObj.getStr("userId"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZDdos::getCreateTime, paramObj.getStr("createTime"));
                }
            }
            List<ZDdos> list = list(wrapper);
            // 转换成导出excel实体
            List<ZDdosExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d, true), ZDdosExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZDdosExcelOutDTO());
            }
            // 第一行标题
            String title = "ddos用户请求记录";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZDdosExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ZDdos> importData =
                ExcelReadUtils.read(request, 1, 2, ZDdos.class, ZDdosExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZDdos> saveData = importData.stream().map(o -> {
            o.setDdosId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
