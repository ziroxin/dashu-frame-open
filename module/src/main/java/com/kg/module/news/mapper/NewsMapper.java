package com.kg.module.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.news.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 新闻表-测试 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2023-02-17
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {

    @Select("SELECT * FROM demo_news WHERE news_id = #{newsId}")
    News getByIdTestSQL(String newsId);

    News getByIdTestXml(String newsId);
}
