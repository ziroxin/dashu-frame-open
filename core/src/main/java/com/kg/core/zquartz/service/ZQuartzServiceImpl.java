package com.kg.core.zquartz.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.TimeUtils;
import com.kg.core.zquartz.dto.ZQuartzDTO;
import com.kg.core.zquartz.entity.ZQuartz;
import com.kg.core.zquartz.excel.ZQuartzExcelConstant;
import com.kg.core.zquartz.excel.ZQuartzExcelOutDTO;
import com.kg.core.zquartz.mapper.ZQuartzMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 定时任务调度表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-12-26
 */
@Service
public class ZQuartzServiceImpl extends ServiceImpl<ZQuartzMapper, ZQuartz> implements ZQuartzService {

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
            QueryWrapper<ZQuartz> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("quartzId")) {
                    wrapper.lambda().eq(ZQuartz::getQuartzId, paramObj.getStr("quartzId"));
                }
                if (paramObj.containsKey("jobName")) {
                    wrapper.lambda().like(ZQuartz::getJobName, paramObj.getStr("jobName"));
                }
                if (paramObj.containsKey("jobClass")) {
                    wrapper.lambda().like(ZQuartz::getJobClass, paramObj.getStr("jobClass"));
                }
                if (paramObj.containsKey("jobTimeCron")) {
                    wrapper.lambda().eq(ZQuartz::getJobTimeCron, paramObj.getStr("jobTimeCron"));
                }
                if (paramObj.containsKey("description")) {
                    wrapper.lambda().eq(ZQuartz::getDescription, paramObj.getStr("description"));
                }
                if (paramObj.containsKey("status")) {
                    wrapper.lambda().eq(ZQuartz::getStatus, paramObj.getStr("status"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(ZQuartz::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(ZQuartz::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZQuartz> list = list(wrapper);
            // 转换成导出excel实体
            List<ZQuartzExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d, true), ZQuartzExcelOutDTO.class))
                    .collect(Collectors.toList());
            // 第一行标题
            String title = "定时任务调度表";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZQuartzExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    @Override
    public boolean isJobExit(ZQuartzDTO zQuartzDTO) {
        if (StringUtils.hasText(zQuartzDTO.getQuartzId())) {
            // 修改查重
            return lambdaQuery().eq(ZQuartz::getJobName, zQuartzDTO.getJobName())
                    .ne(ZQuartz::getQuartzId, zQuartzDTO.getQuartzId()).count() > 0;
        } else {
            // 添加查重
            return lambdaQuery().eq(ZQuartz::getJobName, zQuartzDTO.getJobName()).count() > 0;
        }
    }
}
