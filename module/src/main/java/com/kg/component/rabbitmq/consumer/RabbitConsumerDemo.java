package com.kg.component.rabbitmq.consumer;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import com.kg.component.utils.TimeUtils;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 消费者 - DEMO
 *
 * <p>
 * 下面代码中 @Bean 的作用是主动创建队列：
 * 1. 若队列不存在时，监听时会报错；因此为保证正常监听，主动先创建队列
 * 2. 若您的业务能确保队列一定存在，可删掉@Bean相关代码
 * </p>
 *
 * @author ziro
 * @date 2024/6/14 14:33
 */
@Component
@RabbitIsEnabled
public class RabbitConsumerDemo {

    /**
     * 广播 fanout 队列
     * <p>
     * Queue的name对应 RabbitFanoutProducer中的默认常量值DEFAULT_FANOUT_QUEUE
     * 若是自定义queue(队列)名，这里也需要修改
     * </p>
     */
    @Bean
    public Queue queue1() {
        return new Queue("dashu.fanout.queue");
    }

    /**
     * 直连 direct 队列
     * <p>
     * Queue的name对应 RabbitDirectProducer中的默认常量值 DEFAULT_DIRECT_ROUTE_KEY
     * 若是自定义queue(队列)名，这里也需要修改
     * </p>
     */
    @Bean
    public Queue queue2() {
        return new Queue("dashu.direct.queue");
    }

    /**
     * 主题 topic 队列
     *
     * <p>
     * 主题模式的 Queue 的 name 是可以模糊匹配的，详细配置参考官方文档
     * </p>
     */
    @Bean
    public Queue queue3() {
        return new Queue("dashu.topic.queue");
    }

    /**
     * 延时 delay 队列
     *
     *
     * <p>
     * Queue的name对应RabbitDelayProducer中的默认常量值DEFAULT_DELAY_ROUTE_KEY
     * 若是自定义queue(队列)名，这里也需要修改
     * </p>
     */
    @Bean
    public Queue queue4() {
        return new Queue("dashu.delay.queue");
    }

    /**
     * DEMO 1 广播 fanout / 直连 direct 消费者（同时监听多个队列的demo）
     *
     * <p>
     * queues: 队列名/路由键（可监听一个或多个队列，多用逗号分隔）
     * ackMode: 消息确认模式，可选值有 "AUTO"（自动确认） 和 "MANUAL"（手动确认），默认为 "AUTO"
     * </p>
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = {"dashu.fanout.queue", "dashu.direct.queue"}, ackMode = "AUTO")
    public void receive(String message) {
        System.out.println("接收fanout/direct消息：" + message);
    }

    /**
     * DEMO 2 主题模式消费者
     * <p>
     * queues 可配置主题模式的路由键，"#" 表示匹配一个或多个单词，"*" 表示匹配一个单词
     * </p>
     */
    @RabbitListener(queues = "dashu.topic.queue")
    public void processMessage(String message) {
        System.out.println("接收topic消息：" + message);
    }

    /**
     * DEMO 3 延迟模式消费者
     */
    @RabbitListener(queues = "dashu.delay.queue", ackMode = "AUTO")
    public void delayReceive(String message) {
        System.out.println("接收到延迟消息：" + message);
        System.out.println(TimeUtils.now().toFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    // ============= 监听其他队列，可自行添加 =============
    // ...

}
