package com.kg.core.zorg.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.zorg.entity.ZOrganization;
import com.kg.core.zorg.excels.ZOrganizationExcelConstant;
import com.kg.core.zorg.excels.ZOrganizationExcelOutDTO;
import com.kg.core.zorg.mapper.ZOrganizationMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织机构表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-01-11
 */
@Service
public class ZOrganizationServiceImpl extends ServiceImpl<ZOrganizationMapper, ZOrganization> implements ZOrganizationService {

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
            QueryWrapper<ZOrganization> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("orgId")) {
                    wrapper.lambda().eq(ZOrganization::getOrgId, paramObj.getStr("orgId"));
                }
                if (paramObj.containsKey("orgName")) {
                    wrapper.lambda().eq(ZOrganization::getOrgName, paramObj.getStr("orgName"));
                }
                if (paramObj.containsKey("orgParentId")) {
                    wrapper.lambda().eq(ZOrganization::getOrgParentId, paramObj.getStr("orgParentId"));
                }
                if (paramObj.containsKey("orgPath")) {
                    wrapper.lambda().eq(ZOrganization::getOrgPath, paramObj.getStr("orgPath"));
                }
                if (paramObj.containsKey("orgLevel")) {
                    wrapper.lambda().eq(ZOrganization::getOrgLevel, paramObj.getStr("orgLevel"));
                }
                if (paramObj.containsKey("orderIndex")) {
                    wrapper.lambda().eq(ZOrganization::getOrderIndex, paramObj.getStr("orderIndex"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(ZOrganization::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(ZOrganization::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZOrganization> list = list(wrapper);
            // 转换成导出excel实体
            List<ZOrganizationExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZOrganizationExcelOutDTO.class))
                    .collect(Collectors.toList());
            // 第一行标题
            String title = "组织机构表";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, ZOrganizationExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
