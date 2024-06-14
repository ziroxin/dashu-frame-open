package com.kg.component.rabbitmq.consumer;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 消费者 - DEMO
 *
 * @author ziro
 * @date 2024/6/14 14:33
 */
@Component
@RabbitIsEnabled
public class RabbitConsumerDemo {

    /**
     * 若队列不存在，监听会报错；为保证正常监听，主动先创建队列
     * （若确保队列存在，可注释掉此Bean）
     */
    @Bean
    public Queue queue1() {
        return new Queue("dashu.fanout.queue");
    }

    @Bean
    public Queue queue2() {
        return new Queue("dashu.direct.route");
    }

    /**
     * RabbitMQ的消费者DEMO
     *
     * <p>
     * queues: 队列名/路由键（可监听一个或多个队列，多用逗号分隔）
     * ackMode: 消息确认模式，可选值有 "AUTO"（自动确认） 和 "MANUAL"（手动确认），默认为 "AUTO"
     * </p>
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = {"dashu.fanout.queue", "dashu.direct.route"}, ackMode = "AUTO")
    public void receive(String message) {
        System.out.println("接收消息：" + message);
    }

    // ============= 监听其他队列，可自行添加 =============
    // ...

}
