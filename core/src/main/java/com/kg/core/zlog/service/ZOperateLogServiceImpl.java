package com.kg.core.zlog.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.zlog.entity.ZOperateLog;
import com.kg.core.zlog.excels.ZOperateLogExcelConstant;
import com.kg.core.zlog.excels.ZOperateLogExcelOutDTO;
import com.kg.core.zlog.mapper.ZOperateLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-01-07
 */
@Service
public class ZOperateLogServiceImpl extends ServiceImpl<ZOperateLogMapper, ZOperateLog> implements ZOperateLogService {

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
            QueryWrapper<ZOperateLog> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("logId")) {
                    wrapper.lambda().eq(ZOperateLog::getLogId, paramObj.getStr("logId"));
                }
                if (paramObj.containsKey("userId")) {
                    wrapper.lambda().eq(ZOperateLog::getUserId, paramObj.getStr("userId"));
                }
                if (paramObj.containsKey("userName")) {
                    wrapper.lambda().eq(ZOperateLog::getUserName, paramObj.getStr("userName"));
                }
                if (paramObj.containsKey("logMethod")) {
                    wrapper.lambda().eq(ZOperateLog::getLogMethod, paramObj.getStr("logMethod"));
                }
                if (paramObj.containsKey("logMsg")) {
                    wrapper.lambda().eq(ZOperateLog::getLogMsg, paramObj.getStr("logMsg"));
                }
                if (paramObj.containsKey("content")) {
                    wrapper.lambda().eq(ZOperateLog::getContent, paramObj.getStr("content"));
                }
                if (paramObj.containsKey("actionUrl")) {
                    wrapper.lambda().eq(ZOperateLog::getActionUrl, paramObj.getStr("actionUrl"));
                }
                if (paramObj.containsKey("ip")) {
                    wrapper.lambda().eq(ZOperateLog::getIp, paramObj.getStr("ip"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(ZOperateLog::getCreateTime, paramObj.getStr("createTime"));
                }
            }
            List<ZOperateLog> list = list(wrapper);
            // 转换成导出excel实体
            List<ZOperateLogExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZOperateLogExcelOutDTO.class))
                    .collect(Collectors.toList());
            // 第一行标题
            String title = "操作日志表";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZOperateLogExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
