package com.kg;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.kg.component.rabbitmq.producer.RabbitDirectProducer;
import com.kg.component.rabbitmq.producer.RabbitFanoutProducer;
import com.kg.component.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * RabbitMq测试类
 * @author ziro1
 * @date 2024/6/11 16:55
 */
@SpringBootTest
public class RabbitmqTest {

    @Resource
    private RabbitFanoutProducer fanoutProducer;
    @Resource
    private RabbitDirectProducer directProducer;

    @Test
    public void producer() throws InterruptedException {
        fanoutProducer.send("ziroxin111-----");

        directProducer.send("dict111-----");

        Thread.sleep(5000);
        fanoutProducer.send("ziroxin222-----");
        directProducer.send("dict222-----");
        Thread.sleep(5000);
        fanoutProducer.send("ziroxin333-----");
        directProducer.send("dict333-----");
        Thread.sleep(1000);
        System.out.println("end");
    }

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
