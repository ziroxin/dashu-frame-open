package com.kg.core.zquartz.config;

import com.kg.component.utils.QuartzManagerUtils;
import com.kg.core.zquartz.entity.ZQuartz;
import com.kg.core.zquartz.service.ZQuartzService;
import org.quartz.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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
     * 交给spring，启动时自动执行一次
     */
    @Bean
    public void QuartzInitialization() {
        refreshQuartzList();
    }

    public void refreshQuartzList() {
        try {
            System.out.println("========刷新定时任务========");
            // 查询数据库中，定时任务列表
            List<ZQuartz> list = quartzService.list();
            if (list != null) {
                for (ZQuartz zQuartz : list) {
                    if (StringUtils.hasText(zQuartz.getStatus()) && zQuartz.getStatus().equals("1")) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}