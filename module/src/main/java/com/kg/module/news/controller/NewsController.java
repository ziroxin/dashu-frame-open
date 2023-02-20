package com.kg.module.news.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.component.utils.GuidUtils;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.xss.XssFormatUtil;
import com.kg.module.news.dto.NewsDTO;
import com.kg.module.news.dto.convert.NewsConvert;
import com.kg.module.news.entity.News;
import com.kg.module.news.service.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * <p>
 * 新闻表-测试 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2023-02-17
 */
@RestController
@RequestMapping("/news/news")
@Api(tags = "NewsController", value = "新闻表-测试", description = "新闻表-测试")
public class NewsController {

    @Resource
    private NewsService newsService;
    @Resource
    private NewsConvert newsConvert;

    @ApiOperation(value = "/news/news/getById", notes = "详情-新闻表-测试", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newsId", value = "ID", paramType = "query", required = true, dataType = "String")
    })
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('news:news:getById')")
    public NewsDTO getById(String newsId) {
        return newsConvert.entityToDto(newsService.getById(newsId));
    }

    @ApiOperation(value = "/news/news/list", notes = "分页列表-新闻表-测试", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('news:news:list')")
    public Page<News> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                           @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                           @RequestParam(value = "params", required = false) String params) {
        Page<News> pager = new Page<>(page, limit);
        // 根据条件查询
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

        // 默认排序
        wrapper.lambda().orderByAsc(News::getOrderIndex);
        Page<News> result = newsService.page(pager, wrapper);
        result.setRecords(
                result.getRecords().stream().map(news -> {
                    news.setNewsContent(XssFormatUtil.toHtml(news.getNewsContent()));
                    return news;
                }).collect(Collectors.toList()));
        return result;
    }

    @ApiOperation(value = "/news/news/add", notes = "新增-新闻表-测试", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('news:news:add')")
    @NoRepeatSubmit
    public void add(@RequestBody NewsDTO newsDTO) throws BaseException {
        try {
            News news = newsConvert.dtoToEntity(newsDTO);
            news.setNewsId(GuidUtils.getUuid());
            news.setCreateTime(LocalDateTime.now());
            newsService.save(news);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("新增失败！请重试");
        }
    }

    @ApiOperation(value = "/news/news/update", notes = "修改-新闻表-测试", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('news:news:update')")
    @NoRepeatSubmit
    public void update(@RequestBody NewsDTO newsDTO) throws BaseException {
        try {
            News news = newsConvert.dtoToEntity(newsDTO);
            news.setUpdateTime(LocalDateTime.now());
            newsService.updateById(news);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("修改失败！请重试");
        }
    }

    @ApiOperation(value = "/news/news/delete", notes = "删除-新闻表-测试", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newsIds", value = "待删除id列表", paramType = "query", required = true, dataType = "String")
    })
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('news:news:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] newsIds) throws BaseException {
        try {
            newsService.removeBatchByIds(Arrays.asList(newsIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("删除失败！请重试");
        }
    }

    @ApiOperation(value = "/news/news/export/excel", notes = "导出excel-新闻表-测试", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")
    })
    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('news:news:export:excel')")
    public String exportExcel(@RequestParam(value = "params", required = false) String params) throws BaseException {
        String result = newsService.exportExcel(params);
        if ("error".equals(result)) {
            throw new BaseException("导出Excel失败，请重试！");
        }
        return result;
    }
}
