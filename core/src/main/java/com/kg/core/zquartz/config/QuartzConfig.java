package com.kg.core.zquartz.config;

import cn.hutool.core.thread.ThreadUtil;
import com.kg.component.utils.QuartzManagerUtils;
import com.kg.core.zquartz.entity.ZQuartz;
import com.kg.core.zquartz.service.ZQuartzService;
import org.quartz.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务统一配置
 *
 * @author ziro
 * @date 2022-12-26 16:01:36
 */
@Configuration
public class QuartzConfig {
    // 定时任务管理工具类
    @Resource
    private QuartzManagerUtils quartzManagerUtils;
    // 定时任务查询
    @Resource
    private ZQuartzService quartzService;
    // 已开启的定时任务列表
    private List<String> hasOpenedJobsList = new ArrayList<>();

    /**
     * 配置定时任务
     */
    @Bean
    public void Initialization() {
        System.out.println("========启动定时任务扫描=============");
        ThreadUtil.execute(() -> {
            try {
                while (true) {
                    refreshQuartzList();
                    // 每30s，刷新一次定时任务状态
                    Thread.sleep(30000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void refreshQuartzList() {
        try {
            System.out.println("刷新定时任务");
            // 查询数据库中，定时任务列表
            List<ZQuartz> list = quartzService.list();
            for (ZQuartz zQuartz : list) {
                if (zQuartz.getStatus().equals("1")) {
                    // 开启--------------------
                    if (!hasOpenedJobsList.contains(zQuartz.getJobName())) {
                        // 开启任务
                        quartzManagerUtils.addJob(zQuartz.getJobName(), (Class<Job>) Class.forName(zQuartz.getJobClass()), zQuartz.getJobTimeCron());
                        // 开启成功，加入已开启列表
                        hasOpenedJobsList.add(zQuartz.getJobName());
                    }
                } else {
                    // 关闭--------------------
                    if (hasOpenedJobsList.contains(zQuartz.getJobName())) {
                        // 已开启的，才需要关闭
                        quartzManagerUtils.removeJob(zQuartz.getJobName());
                        // 关闭成功，移除列表
                        hasOpenedJobsList.remove(zQuartz.getJobName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}