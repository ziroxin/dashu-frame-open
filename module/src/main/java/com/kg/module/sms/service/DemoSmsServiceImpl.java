package com.kg.module.sms.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.sms.aliyun.AliyunSmsResult;
import com.kg.component.sms.aliyun.AliyunSmsSender;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.TimeUtils;
import com.kg.core.exception.BaseException;
import com.kg.module.sms.dto.DemoSmsDTO;
import com.kg.module.sms.dto.convert.DemoSmsConvert;
import com.kg.module.sms.entity.DemoSms;
import com.kg.module.sms.excels.DemoSmsExcelConstant;
import com.kg.module.sms.excels.DemoSmsExcelOutDTO;
import com.kg.module.sms.mapper.DemoSmsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 短信 - demo 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-12-14
 */
@Service
public class DemoSmsServiceImpl extends ServiceImpl<DemoSmsMapper, DemoSms> implements DemoSmsService {

    @Resource
    private DemoSmsConvert demoSmsConvert;
    @Resource
    private AliyunSmsSender aliyunSmsSender;


    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<DemoSmsDTO> pagelist(Integer page, Integer limit, String params) throws BaseException {
        Page<DemoSms> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<DemoSms> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("smsId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("smsId")), DemoSms::getSmsId, paramObj.getStr("smsId"));
            }
            if (paramObj.containsKey("smsChannel")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("smsChannel")), DemoSms::getSmsChannel, paramObj.getStr("smsChannel"));
            }
            if (paramObj.containsKey("smsPhones")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("smsPhones")), DemoSms::getSmsPhones, paramObj.getStr("smsPhones"));
            }
            if (paramObj.containsKey("sendJson")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("sendJson")), DemoSms::getSendJson, paramObj.getStr("sendJson"));
            }
            if (paramObj.containsKey("status")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("status")), DemoSms::getStatus, paramObj.getStr("status"));
            }
            if (paramObj.containsKey("resultJson")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("resultJson")), DemoSms::getResultJson, paramObj.getStr("resultJson"));
            }
            if (paramObj.containsKey("createTime")) {
                if (StringUtils.hasText(paramObj.getStr("createTime"))) {
                    try {
                        String start = TimeUtils.setTime(paramObj.getStr("createTime"), "yyyy-MM-dd").toFormat("yyyy-MM-dd 00:00:00");
                        String end = TimeUtils.setTime(paramObj.getStr("createTime"), "yyyy-MM-dd").toFormat("yyyy-MM-dd 23:59:59");
                        wrapper.lambda().between(DemoSms::getCreateTime, start, end);
                    } catch (ParseException e) {
                        throw new BaseException("createTime时间格式不正确！");
                    }
                }
            }
        }
        //排序
        wrapper.lambda().orderByDesc(DemoSms::getCreateTime);
        //返回数据
        Page<DemoSms> pageEntity = page(pager, wrapper);
        if (pageEntity.getTotal() == 0) {
            return new Page<>();
        }
        Page<DemoSmsDTO> result = new Page<>();
        result.setRecords(pageEntity.getRecords().stream().map(m -> {
            DemoSmsDTO demoSmsDTO = demoSmsConvert.entityToDto(m);
            return demoSmsDTO;
        }).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param demoSmsDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(DemoSmsDTO demoSmsDTO) throws BaseException {
        DemoSms demoSms = demoSmsConvert.dtoToEntity(demoSmsDTO);
        demoSms.setSmsId(GuidUtils.getUuid());
        demoSms.setCreateTime(LocalDateTime.now());
        // 发送短信
        if (StringUtils.hasText(demoSmsDTO.getSmsChannel()) && "阿里云短信".equals(demoSmsDTO.getSmsChannel())) {
            AliyunSmsResult sendResult = aliyunSmsSender.send(demoSmsDTO.getSmsTemplate(),
                    demoSmsDTO.getSmsPhones(), demoSmsDTO.getSendJson());
            if (sendResult.isSuccess()) {
                // 发送成功
                demoSms.setStatus("1");
                demoSms.setResultJson(JSONUtil.toJsonStr(sendResult.getResultJson()));
                save(demoSms);
            } else {
                // 发送失败
                demoSms.setStatus("0");
                demoSms.setResultJson(JSONUtil.toJsonStr(sendResult.getResultJson()));
                save(demoSms);
                throw new BaseException("发送失败，错误信息：" + sendResult.getErrorMessage());
            }
        }
    }

    /**
     * 修改
     *
     * @param demoSmsDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(DemoSmsDTO demoSmsDTO) {
        DemoSms demoSms = demoSmsConvert.dtoToEntity(demoSmsDTO);
        updateById(demoSms);
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
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<DemoSms> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("smsId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("smsId")), DemoSms::getSmsId, paramObj.getStr("smsId"));
                }
                if (paramObj.containsKey("smsChannel")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("smsChannel")), DemoSms::getSmsChannel, paramObj.getStr("smsChannel"));
                }
                if (paramObj.containsKey("smsPhones")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("smsPhones")), DemoSms::getSmsPhones, paramObj.getStr("smsPhones"));
                }
                if (paramObj.containsKey("sendJson")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("sendJson")), DemoSms::getSendJson, paramObj.getStr("sendJson"));
                }
                if (paramObj.containsKey("status")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("status")), DemoSms::getStatus, paramObj.getStr("status"));
                }
                if (paramObj.containsKey("resultJson")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("resultJson")), DemoSms::getResultJson, paramObj.getStr("resultJson"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), DemoSms::getCreateTime, paramObj.getStr("createTime"));
                }
            }
            List<DemoSms> list = list(wrapper);
            // 转换成导出excel实体
            List<DemoSmsExcelOutDTO> dataList = list.stream().map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), DemoSmsExcelOutDTO.class)).collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new DemoSmsExcelOutDTO());
            }
            // 第一行标题
            String title = "短信 - demo";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, DemoSmsExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<DemoSms> importData = ExcelCommonUtils.read(request, 1, 2, DemoSms.class, DemoSmsExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<DemoSms> saveData = importData.stream().map(o -> {
            o.setSmsId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
