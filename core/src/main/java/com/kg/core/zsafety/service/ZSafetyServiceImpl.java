package com.kg.core.zsafety.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.excels.ZSafetyExcelConstant;
import com.kg.core.zsafety.excels.ZSafetyExcelOutDTO;
import com.kg.core.zsafety.mapper.ZSafetyMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 密码安全等设置 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-12-30
 */
@Service
public class ZSafetyServiceImpl extends ServiceImpl<ZSafetyMapper, ZSafety> implements ZSafetyService {

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
            QueryWrapper<ZSafety> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("id")) {
                    wrapper.lambda().eq(ZSafety::getId, paramObj.getStr("id"));
                }
                if (paramObj.containsKey("startLength")) {
                    wrapper.lambda().eq(ZSafety::getStartLength, paramObj.getStr("startLength"));
                }
                if (paramObj.containsKey("endLength")) {
                    wrapper.lambda().eq(ZSafety::getEndLength, paramObj.getStr("endLength"));
                }
                if (paramObj.containsKey("lowercase")) {
                    wrapper.lambda().eq(ZSafety::getLowercase, paramObj.getStr("lowercase"));
                }
                if (paramObj.containsKey("uppercase")) {
                    wrapper.lambda().eq(ZSafety::getUppercase, paramObj.getStr("uppercase"));
                }
                if (paramObj.containsKey("numbers")) {
                    wrapper.lambda().eq(ZSafety::getNumbers, paramObj.getStr("numbers"));
                }
                if (paramObj.containsKey("specialCharacters")) {
                    wrapper.lambda().eq(ZSafety::getSpecialCharacters, paramObj.getStr("specialCharacters"));
                }
                if (paramObj.containsKey("banUsername")) {
                    wrapper.lambda().eq(ZSafety::getBanUsername, paramObj.getStr("banUsername"));
                }
                if (paramObj.containsKey("validTime")) {
                    wrapper.lambda().eq(ZSafety::getValidTime, paramObj.getStr("validTime"));
                }
                if (paramObj.containsKey("prompt")) {
                    wrapper.lambda().eq(ZSafety::getPrompt, paramObj.getStr("prompt"));
                }
                if (paramObj.containsKey("loginFailedTimes")) {
                    wrapper.lambda().eq(ZSafety::getLoginFailedTimes, paramObj.getStr("loginFailedTimes"));
                }
                if (paramObj.containsKey("lockTime")) {
                    wrapper.lambda().eq(ZSafety::getLockTime, paramObj.getStr("lockTime"));
                }
                if (paramObj.containsKey("defaultPassword")) {
                    wrapper.lambda().eq(ZSafety::getDefaultPassword, paramObj.getStr("defaultPassword"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(ZSafety::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZSafety> list = list(wrapper);
            // 转换成导出excel实体
            List<ZSafetyExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZSafetyExcelOutDTO.class))
                    .collect(Collectors.toList());
            // 第一行标题
            String title = "密码安全等设置";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, ZSafetyExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
