package com.kg.module.messageTo.service;

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
import com.kg.module.messageTo.dto.ZMessageToDTO;
import com.kg.module.messageTo.dto.convert.ZMessageToConvert;
import com.kg.module.messageTo.entity.ZMessageTo;
import com.kg.module.messageTo.excels.ZMessageToExcelConstant;
import com.kg.module.messageTo.excels.ZMessageToExcelOutDTO;
import com.kg.module.messageTo.mapper.ZMessageToMapper;
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
 * 消息发送至 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@Service
public class ZMessageToServiceImpl extends ServiceImpl<ZMessageToMapper, ZMessageTo> implements ZMessageToService {

    @Resource
    private ZMessageToConvert zMessageToConvert;


    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZMessageToDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZMessageTo> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZMessageTo> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("toId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("toId")), ZMessageTo::getToId, paramObj.getStr("toId"));
            }
            if (paramObj.containsKey("msgId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgId")), ZMessageTo::getMsgId, paramObj.getStr("msgId"));
            }
            if (paramObj.containsKey("toUserId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("toUserId")), ZMessageTo::getToUserId, paramObj.getStr("toUserId"));
            }
            if (paramObj.containsKey("sendUserId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("sendUserId")), ZMessageTo::getSendUserId, paramObj.getStr("sendUserId"));
            }
            if (paramObj.containsKey("msgStatus")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgStatus")), ZMessageTo::getMsgStatus, paramObj.getStr("msgStatus"));
            }
            if (paramObj.containsKey("readTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("readTime")), ZMessageTo::getReadTime, paramObj.getStr("readTime"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZMessageTo::getCreateTime, paramObj.getStr("createTime"));
            }
        }
        //返回数据
        Page<ZMessageTo> pageEntity = page(pager, wrapper);
        if (pageEntity.getTotal() == 0) {
            return new Page<>();
        }
        Page<ZMessageToDTO> result = new Page<>();
        result.setRecords(pageEntity.getRecords().stream()
                .map(m -> {
                    ZMessageToDTO zMessageToDTO = zMessageToConvert.entityToDto(m);
                    return zMessageToDTO;
                })
                .collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zMessageToDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZMessageToDTO zMessageToDTO) {
        ZMessageTo zMessageTo = zMessageToConvert.dtoToEntity(zMessageToDTO);
        zMessageTo.setToId(GuidUtils.getUuid());
        zMessageTo.setCreateTime(LocalDateTime.now());
        save(zMessageTo);
    }

    /**
     * 修改
     *
     * @param zMessageToDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZMessageToDTO zMessageToDTO) {
        ZMessageTo zMessageTo = zMessageToConvert.dtoToEntity(zMessageToDTO);
        updateById(zMessageTo);
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
            QueryWrapper<ZMessageTo> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("toId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("toId")), ZMessageTo::getToId, paramObj.getStr("toId"));
                }
                if (paramObj.containsKey("msgId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgId")), ZMessageTo::getMsgId, paramObj.getStr("msgId"));
                }
                if (paramObj.containsKey("toUserId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("toUserId")), ZMessageTo::getToUserId, paramObj.getStr("toUserId"));
                }
                if (paramObj.containsKey("sendUserId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("sendUserId")), ZMessageTo::getSendUserId, paramObj.getStr("sendUserId"));
                }
                if (paramObj.containsKey("msgStatus")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgStatus")), ZMessageTo::getMsgStatus, paramObj.getStr("msgStatus"));
                }
                if (paramObj.containsKey("readTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("readTime")), ZMessageTo::getReadTime, paramObj.getStr("readTime"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZMessageTo::getCreateTime, paramObj.getStr("createTime"));
                }
            }
            List<ZMessageTo> list = list(wrapper);
            // 转换成导出excel实体
            List<ZMessageToExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d, true), ZMessageToExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZMessageToExcelOutDTO());
            }
            // 第一行标题
            String title = "消息发送至";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZMessageToExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ZMessageTo> importData =
                ExcelReadUtils.read(request, 1, 2, ZMessageTo.class, ZMessageToExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZMessageTo> saveData = importData.stream().map(o -> {
            o.setToId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
