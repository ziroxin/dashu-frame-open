package com.kg.rabbitmq;

import com.kg.component.rabbitmq.producer.RabbitDelayedProducer;
import com.kg.component.rabbitmq.producer.RabbitTopicProducer;
import com.kg.component.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * RabbitMq测试类
 *
 * @author ziro1
 * @date 2024/6/11 16:55
 */
@SpringBootTest
public class RabbitmqTest {

    //    @Resource
//    private RabbitFanoutProducer fanoutProducer;
//    @Resource
//    private RabbitDirectProducer directProducer;
//    @Resource
//    private RabbitTopicProducer topicProducer;
    @Resource
    private RabbitDelayedProducer delayedProducer;

    @Test
    public void producer() throws InterruptedException {
        /*
        // 广播消息
        fanoutProducer.send("ziroxin111-----");
        // 直连消息
        directProducer.send("dict111-----");
        // 主题消息
        String topicQueue = "dashu.topic.queue";
        topicProducer.send("dashu.*.queue消息", "dashu.*.queue", topicQueue);

        // 等待5秒再发送一遍
        Thread.sleep(5000);
        fanoutProducer.send("ziroxin222-----");
        directProducer.send("dict222-----");
        topicProducer.send("dashu.#消息", "dashu.#", topicQueue);

        // 等待5秒再发送一遍
        Thread.sleep(5000);
        fanoutProducer.send("ziroxin333-----");
        directProducer.send("dict333-----");
        topicProducer.send("dashu.*.*消息", "dashu.*.*", topicQueue);
        Thread.sleep(1000);
        System.out.println("end");*/

        // 延时消息
        System.out.println("（延迟10秒）发消息时间：" + TimeUtils.now().toFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        delayedProducer.send("ziroxin444-----", 10);// 延迟10秒发消息
        // 等待15秒，等接收消息
        Thread.sleep(15000);
    }


}
