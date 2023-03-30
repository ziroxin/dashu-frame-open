package com.kg.module.atest.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.module.atest.dto.ATestDTO;
import com.kg.module.atest.dto.convert.ATestConvert;
import com.kg.module.atest.entity.ATest;
import com.kg.module.atest.excels.ATestExcelConstant;
import com.kg.module.atest.excels.ATestExcelOutDTO;
import com.kg.module.atest.mapper.ATestMapper;
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
 * 测试表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-03-30
 */
@Service
public class ATestServiceImpl extends ServiceImpl<ATestMapper, ATest> implements ATestService {

    @Resource
    private ATestConvert aTestConvert;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ATestDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ATest> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ATest> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("testId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testId")), ATest::getTestId, paramObj.getStr("testId"));
            }
            if (paramObj.containsKey("testName")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testName")), ATest::getTestName, paramObj.getStr("testName"));
            }
            if (paramObj.containsKey("testAge")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testAge")), ATest::getTestAge, paramObj.getStr("testAge"));
            }
            if (paramObj.containsKey("testSex")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testSex")), ATest::getTestSex, paramObj.getStr("testSex"));
            }
            if (paramObj.containsKey("orderIndex")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ATest::getOrderIndex, paramObj.getStr("orderIndex"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ATest::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ATest::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        // 默认排序
        wrapper.lambda().orderByAsc(ATest::getOrderIndex);
        //返回数据
        Page<ATest> pageEntity = page(pager, wrapper);
        Page<ATestDTO> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> aTestConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param aTestDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ATestDTO aTestDTO) {
        ATest aTest = aTestConvert.dtoToEntity(aTestDTO);
        aTest.setTestId(GuidUtils.getUuid());
        aTest.setCreateTime(LocalDateTime.now());
        save(aTest);
    }

    /**
     * 修改
     *
     * @param aTestDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ATestDTO aTestDTO) {
        ATest aTest = aTestConvert.dtoToEntity(aTestDTO);
        aTest.setUpdateTime(LocalDateTime.now());
        updateById(aTest);
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
            QueryWrapper<ATest> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("testId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testId")), ATest::getTestId, paramObj.getStr("testId"));
                }
                if (paramObj.containsKey("testName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testName")), ATest::getTestName, paramObj.getStr("testName"));
                }
                if (paramObj.containsKey("testAge")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testAge")), ATest::getTestAge, paramObj.getStr("testAge"));
                }
                if (paramObj.containsKey("testSex")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("testSex")), ATest::getTestSex, paramObj.getStr("testSex"));
                }
                if (paramObj.containsKey("orderIndex")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("orderIndex")), ATest::getOrderIndex, paramObj.getStr("orderIndex"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ATest::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ATest::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ATest> list = list(wrapper);
            // 转换成导出excel实体
            List<ATestExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ATestExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ATestExcelOutDTO());
            }
            // 第一行标题
            String title = "测试表";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, ATestExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ATest> importData =
                ExcelCommonUtils.read(request, 1, 2, ATest.class, ATestExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ATest> saveData = importData.stream().map(o -> {
            o.setTestId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
