package com.kg.component.rabbitmq.producer;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ Topic模式工具类
 *
 * @author ziro
 * @date 2024/6/13 15:27
 */
@Component
@RabbitIsEnabled
public class RabbitTopicProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RabbitBinding binding;

    /**
     * 默认 topic exchange 名称
     */
    private static final String DEFAULT_TOPIC_EXCHANGE = "dashu.topic.exchange";

    /**
     * 发消息 - 指定路由
     *
     * @param message  消息内容
     * @param routeKey 路由表达式
     * @param queue    队列名称
     */
    public void send(String message, String routeKey, String queue) {
        send(message, routeKey, queue, DEFAULT_TOPIC_EXCHANGE);
    }

    /**
     * 发消息 - 指定路由、指定交换机
     *
     * @param message  消息内容
     * @param routeKey 路由表达式
     * @param queue    队列名称
     * @param exchange 交换机名称
     */
    public void send(String message, String routeKey, String queue, String exchange) {
        // 绑定队列到交换机
        // 注意：若无队列或交换机或绑定关系，直接[发消息]会报错，防止意外，先绑定一次
        binding.bindTopic(exchange, routeKey, queue);
        // topic 模式，必须指定表达式的routingKey
        rabbitTemplate.convertAndSend(exchange, routeKey, message);
    }
}
