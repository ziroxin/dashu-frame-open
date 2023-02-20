package com.kg.module.news.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.module.news.entity.News;
import com.kg.module.news.excels.NewsExcelConstant;
import com.kg.module.news.excels.NewsExcelOutDTO;
import com.kg.module.news.mapper.NewsMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 新闻表-测试 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-02-17
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

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
            QueryWrapper<News> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("newsId")) {
                    wrapper.lambda().eq(News::getNewsId, paramObj.getStr("newsId"));
                }
                if (paramObj.containsKey("newsTitle")) {
                    wrapper.lambda().eq(News::getNewsTitle, paramObj.getStr("newsTitle"));
                }
                if (paramObj.containsKey("newsContent")) {
                    wrapper.lambda().eq(News::getNewsContent, paramObj.getStr("newsContent"));
                }
                if (paramObj.containsKey("orderIndex")) {
                    wrapper.lambda().eq(News::getOrderIndex, paramObj.getStr("orderIndex"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(News::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(News::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<News> list = list(wrapper);
            // 转换成导出excel实体
            List<NewsExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), NewsExcelOutDTO.class))
                    .collect(Collectors.toList());
            // 第一行标题
            String title = "新闻表-测试";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, NewsExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
