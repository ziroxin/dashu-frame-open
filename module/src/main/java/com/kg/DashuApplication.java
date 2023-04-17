package com.kg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// 开启声明式事务
@EnableTransactionManagement
// 扫描Mapper
@MapperScan("com.kg.**.mapper")
// 多模块项目，配置要扫描的包
@SpringBootApplication(scanBasePackages = "com.kg.**")
public class DashuApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashuApplication.class, args);
    }

}
