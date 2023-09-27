package com.kg;

import cn.hutool.json.JSONUtil;
import com.kg.module.news.mapper.NewsMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author ziro
 * @date 2023/9/27 15:19
 */
@SpringBootTest
public class Test2 {
    @Resource
    private NewsMapper mapper;

    @Test
    public void test() {
        System.out.println("xml 方式");
        System.out.println(JSONUtil.toJsonStr(mapper.getByIdTestXml("4585d26d-1201-400f-8ab3-6e7d65310ee8")));

        System.out.println("mybatis plus 方式");
        System.out.println(JSONUtil.toJsonStr(mapper.selectById("4585d26d-1201-400f-8ab3-6e7d65310ee8")));

        System.out.println("注解方式");
        System.out.println(JSONUtil.toJsonStr(mapper.getByIdTestSQL("4585d26d-1201-400f-8ab3-6e7d65310ee8")));
    }
}
