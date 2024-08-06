package com.kg.other;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.kg.component.utils.TimeUtils;

/**
 * @author ziro
 * @date 2024/8/6 17:32
 */
public class OtherTest {
    /**
     * Hutool的 - 线程级定时任务 - 测试
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start(true);

        for (int i = 0; i < 10; i++) {
            String runtimeStr = TimeUtils.now().addSecond(10).toFormat("s m H d M *");
            // 定时任务
            int finalI = i;
            System.out.println(i + "=" + runtimeStr);
            CronUtil.schedule(runtimeStr, (Task) () -> {
                System.out.println("abcdefg" + finalI + "=======" + runtimeStr);
                System.out.println("当前线程ID：" + Thread.currentThread().getId());
            });
            Thread.sleep(1000);
        }

        System.out.println("11");
        System.out.println("当前线程ID：" + Thread.currentThread().getId());

        Thread.sleep(15000);

        System.out.println("2");
        System.out.println("当前线程ID：" + Thread.currentThread().getId());
    }
}
